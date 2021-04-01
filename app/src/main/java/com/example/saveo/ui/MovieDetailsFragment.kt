package com.example.saveo.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.myapiservicesmodule.di.models.ResponseMovieDetails
import com.example.saveo.R
import com.example.saveo.databinding.FragmentMovieDetailsBinding
import com.example.saveo.utils.DataState
import com.example.saveo.viewmodels.ActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieDetailsFragment : Fragment() {
    lateinit var fragmentMovieDetailsBinding: FragmentMovieDetailsBinding
    private val activityViewModel: ActivityViewModel by viewModels()
    var imdb_id: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imdb_id = it.getString("imdb_id")
        }
        activityViewModel.getMovieDetails(
            ActivityViewModel.MainStateEvent.GetMovieDetailsResponse,
            imdb_id
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentMovieDetailsBinding =
            FragmentMovieDetailsBinding.inflate(inflater, container, false)
        return fragmentMovieDetailsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeMovieDetailsDataFromApi()
    }

    fun observeMovieDetailsDataFromApi() {
        activityViewModel.movieDetailsLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is DataState.Success<ResponseMovieDetails> -> {
                    fragmentMovieDetailsBinding.progressbar.visibility = View.GONE
                    setDataInViews(it.data)
                }
                is DataState.Error -> {
                    Toast.makeText(requireActivity(), "Something went wrong or else please check internet", Toast.LENGTH_LONG)
                        .show()
                    fragmentMovieDetailsBinding.progressbar.visibility = View.GONE
                }
                is DataState.Loading -> {
                    if (!fragmentMovieDetailsBinding.progressbar.isVisible)
                        fragmentMovieDetailsBinding.progressbar.visibility = View.VISIBLE
                }
            }
        })
    }

    fun setDataInViews(movieDetails: ResponseMovieDetails) {
        Glide.with(requireActivity()).load(movieDetails.poster).error(R.drawable.placeholder)
            .into(fragmentMovieDetailsBinding.ivPoster)
        fragmentMovieDetailsBinding.tvMovieName.text = movieDetails.title
        fragmentMovieDetailsBinding.tvDirector.text = movieDetails.director
        fragmentMovieDetailsBinding.tvActor.text = movieDetails.actors
        fragmentMovieDetailsBinding.tvYear.text = movieDetails.released
        fragmentMovieDetailsBinding.tvGenre.text = movieDetails.genre
        fragmentMovieDetailsBinding.tvLanguage.text = movieDetails.language
        fragmentMovieDetailsBinding.tvWriter.text = movieDetails.Writer

    }
}