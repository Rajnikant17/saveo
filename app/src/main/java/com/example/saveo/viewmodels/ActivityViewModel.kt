package com.example.saveo.viewmodels

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapiservicesmodule.di.models.Movie
import com.example.myapiservicesmodule.di.models.ResponseMovieDetails
import com.example.myapiservicesmodule.di.models.ResponseMovieList
import com.example.saveo.repository.BusinessLogic
import com.example.saveo.utils.DataState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ActivityViewModel
@ViewModelInject
constructor(application: Application, private val apiCallBusinessLogic: BusinessLogic) :
    AndroidViewModel(application) {
    var page_count = 1
    var max_page_count = 10
    var finalmovieList = ArrayList<Movie>()
    var movieListLiveData: MutableLiveData<DataState<ResponseMovieList>> = MutableLiveData()
    var movieDetailsLiveData: MutableLiveData<DataState<ResponseMovieDetails>> = MutableLiveData()
    fun getMovieList(mainStateEvent: MainStateEvent) {
        viewModelScope.launch {
            when (mainStateEvent) {
                is MainStateEvent.GetMovieListResponse -> {
                    if (page_count <= max_page_count) {
                        apiCallBusinessLogic.executeMovieList(page_count).onEach { dataState ->
                            movieListLiveData.value = dataState
                        }.launchIn(viewModelScope)
                    }
                }
            }
        }
    }

    fun getMovieDetails(mainStateEvent: MainStateEvent, imdb_id: String?) {
        viewModelScope.launch {
            when (mainStateEvent) {
                is MainStateEvent.GetMovieDetailsResponse -> {
                    apiCallBusinessLogic.executeMovieDetails(imdb_id).onEach { dataState ->
                        movieDetailsLiveData.value = dataState
                    }.launchIn(viewModelScope)
                }
            }
        }
    }


    sealed class MainStateEvent {
        object GetMovieListResponse : MainStateEvent()
        object GetMovieDetailsResponse : MainStateEvent()
    }
}