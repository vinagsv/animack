package com.vinag.animack.ui.fragments.anime.viewpagerfrag

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.vinag.animack.databinding.FragmentInfoBinding
import com.vinag.animack.models.anime.Data
import com.vinag.animack.viewmodels.anime.AnimeInfoFragmentViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import java.util.*


class InfoFragment : Fragment(){

    private lateinit var binding : FragmentInfoBinding
    private lateinit var animeInfoVM : AnimeInfoFragmentViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        animeInfoVM = ViewModelProvider(this).get(AnimeInfoFragmentViewModel::class.java)


    }

    override fun onDestroy() {
        super.onDestroy()
        binding.vvTrailer.release()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingState()

        Handler().postDelayed({
            val id = requireActivity().intent.getIntExtra("MAL_ID",0)
            if(id != 0){ //meaning the id exist
                animeInfoVM.getAnimeById(id)
                animeInfoVM.anime.observeOnce(viewLifecycleOwner){ animeData ->

                    bindDataToView(animeData)
                    successState()
                }
            }else{
                Log.e("CHAR_INFO_FRAGMENT", "An error occurred.")
            }
        },500)
    }

    private fun bindDataToView(animeData: Data) {
        binding.apply {
            tvOriginalTitle.text = animeData.title
            tvEnglishTitle.text = animeData.title_english
            tvJapaneseTitle.text = animeData.title_japanese
            tvEpisodes.text = animeData.episodes.toString()
            tvStatus.text = animeData.status
            tvAired.text = extractDate(animeData)
            tvRating.text = animeData.rating
            tvScore.text = animeData.score.toString()
            tvSynopsis.text = animeData.synopsis


            if (animeData.trailer.youtube_id != null) {
                Toast.makeText(activity, "Original Trailer", Toast.LENGTH_SHORT).show()
                playTrailer(animeData.trailer.youtube_id)
            } else {
                //search for video on youtube using api]
                Toast.makeText(activity, "Alternate Trailer", Toast.LENGTH_LONG).show()
                animeInfoVM.getYoutubeTrailerID("${animeData.title} anime trailer")
                animeInfoVM.youtubeID.observe(viewLifecycleOwner){
                    for(item in it){
                        playTrailer(item.id.videoId)
                    }
                }
            }

        }
    }


    private fun playTrailer(youtubeID: String) {
        val youTubePlayerView: YouTubePlayerView = binding.vvTrailer
        viewLifecycleOwner.lifecycle.addObserver(youTubePlayerView)

        youTubePlayerView.getYouTubePlayerWhenReady(object : YouTubePlayerCallback{
            override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                youTubePlayer.cueVideo(youtubeID, 0f)
            }
        })
    }

    private fun extractDate(data : Data) : String{
        val fromMonth = data.aired.prop.from.month
        val fromDay = data.aired.prop.from.day
        val fromYear = data.aired.prop.from.year

        val toMonth = data.aired.prop.to.month
        val toDay = data.aired.prop.to.day
        val toYear = data.aired.prop.to.year

        return "From $fromYear-$fromMonth-$fromDay To $toYear-$toMonth-$toDay"
    }

    private fun successState() {

        binding.apply {
            progressBar.visibility = View.INVISIBLE
            vvTrailer.visibility = View.VISIBLE
            tvJapaneseTitle.visibility = View.VISIBLE
            tvJapaneseTitleLabel.visibility = View.VISIBLE
            tvEnglishTitle.visibility = View.VISIBLE
            tvEnglishTitleLabel.visibility = View.VISIBLE
            tvOriginalTitle.visibility = View.VISIBLE
            tvOriginalTitleLabel.visibility = View.VISIBLE
            tvEpisodesLabel.visibility = View.VISIBLE
            tvEpisodes.visibility = View.VISIBLE
            tvStatusLabel.visibility = View.VISIBLE
            tvStatus.visibility = View.VISIBLE
            tvAiredLabel.visibility = View.VISIBLE
            tvAired.visibility = View.VISIBLE
            tvRatingLabel.visibility = View.VISIBLE
            tvRating.visibility = View.VISIBLE
            tvScoreLabel.visibility = View.VISIBLE
            tvScore.visibility = View.VISIBLE
            tvSynopsisLabel.visibility = View.VISIBLE
            tvSynopsis.visibility = View.VISIBLE
        }
    }
    private fun loadingState() {
        binding.apply {
            vvTrailer.visibility = View.INVISIBLE
            progressBar.visibility = View.VISIBLE
            tvJapaneseTitle.visibility = View.INVISIBLE
            tvJapaneseTitleLabel.visibility = View.INVISIBLE
            tvEnglishTitle.visibility = View.INVISIBLE
            tvEnglishTitleLabel.visibility = View.INVISIBLE
            tvOriginalTitle.visibility = View.INVISIBLE
            tvOriginalTitleLabel.visibility = View.INVISIBLE
            tvEpisodesLabel.visibility = View.INVISIBLE
            tvEpisodes.visibility = View.INVISIBLE
            tvStatusLabel.visibility = View.INVISIBLE
            tvStatus.visibility = View.INVISIBLE
            tvAiredLabel.visibility = View.INVISIBLE
            tvAired.visibility = View.INVISIBLE
            tvRatingLabel.visibility = View.INVISIBLE
            tvRating.visibility = View.INVISIBLE
            tvScoreLabel.visibility = View.INVISIBLE
            tvScore.visibility = View.INVISIBLE
            tvSynopsisLabel.visibility = View.INVISIBLE
            tvSynopsis.visibility = View.INVISIBLE
        }
    }

    private fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(t: T?) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }
}