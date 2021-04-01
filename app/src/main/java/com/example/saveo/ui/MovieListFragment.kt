package com.example.saveo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapiservicesmodule.di.models.Movie
import com.example.myapiservicesmodule.di.models.ResponseMovieList
import com.example.saveo.R
import com.example.saveo.adapter.MovieListAdapter
import com.example.saveo.databinding.FragmentMovieListBinding
import com.example.saveo.utils.DataState
import com.example.saveo.utils.EndlessRecyclerOnScrollLisitener
import com.example.saveo.viewmodels.ActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieListFragment : Fragment(), MovieListAdapter.ClickEventInterface {
    private val activityViewModel: ActivityViewModel by viewModels()
    lateinit var fragmentMovieListBinding: FragmentMovieListBinding
    var movieListAdapter: MovieListAdapter? = null
    var gridLayoutManager: GridLayoutManager? = null
    lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityViewModel.getMovieList(ActivityViewModel.MainStateEvent.GetMovieListResponse)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentMovieListBinding = FragmentMovieListBinding.inflate(inflater, container, false)
        return fragmentMovieListBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        movieListAdapter = null
        gridLayoutManager = GridLayoutManager(requireActivity(), 2)
        observeMovieListDataFromApi()

        //Recyclerview endscroll for pagination
        fragmentMovieListBinding.rvParent.addOnScrollListener(object :
            EndlessRecyclerOnScrollLisitener(gridLayoutManager!!) {
            override fun onScrolledToEnd() {
                activityViewModel.getMovieList(ActivityViewModel.MainStateEvent.GetMovieListResponse)
            }
        })
    }

    fun observeMovieListDataFromApi() {
        activityViewModel.movieListLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is DataState.Success<ResponseMovieList> -> {
                    fragmentMovieListBinding.progressbar.visibility = View.GONE
                    it.data.movieList?.let { it1 -> setParentAdapter(it1) }
                    activityViewModel.page_count++
                }
                is DataState.Error -> {
                    Toast.makeText(requireActivity(), "Something went wrong or else please check internet", Toast.LENGTH_LONG)
                        .show()
                    fragmentMovieListBinding.progressbar.visibility = View.GONE
                }
                is DataState.Loading -> {
                    if (!fragmentMovieListBinding.progressbar.isVisible)
                        fragmentMovieListBinding.progressbar.visibility = View.VISIBLE
                }
            }
        })
    }

    fun setParentAdapter(movieList: List<Movie>) {
        activityViewModel.finalmovieList.addAll(movieList)
        if (movieListAdapter == null) {
            fragmentMovieListBinding.rvParent.setLayoutManager(gridLayoutManager)
            movieListAdapter =
                MovieListAdapter(
                    requireActivity(),
                    activityViewModel.finalmovieList, this@MovieListFragment
                )
            fragmentMovieListBinding.rvParent.adapter = movieListAdapter
        } else {
            movieListAdapter?.notifyDataSetChanged()
        }
    }

    override fun clickEvent(position: Int) {
        val bundle = Bundle()
        bundle.putString("imdb_id", activityViewModel.finalmovieList.get(position).imdbID)
        navController.navigate(R.id.action_movieListFragment_to_movieDetailsFragment, bundle)
    }
}