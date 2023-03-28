package com.example.tvseries.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tvseries.api.Api
import com.example.tvseries.entities.Show
import com.example.tvseries.utils.NetworkUtils
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Response
import com.example.tvseries.entities.Episode
import com.example.tvseries.entities.Season
import com.example.tvseries.entities.ShowFilter

class VMShow: ViewModel() {

    enum class RequestStatus { Idle, Fetching, Done, Error }

    private lateinit var job: Job


    private val _shows = MutableLiveData<List<Show>>(null)
    private val _episodes = MutableLiveData<List<Episode>>(null)
    private val _episodesBySeason = MutableLiveData<List<Episode>>(null)
    private val _seasons = MutableLiveData<List<Season>>()
    private var _requestStatus = MutableLiveData(RequestStatus.Idle)
    private var _selectedShow: Show? = null
    private var _selectedSeason: Season? = null
    private var _selectedPositionSeason: Int? = null
    private var _lastPageIndex: Int = -1

    val shows: LiveData<List<Show>?>
        get() = _shows
    val episodesBySeason: LiveData<List<Episode>?>
        get() = _episodesBySeason
    val seasons: LiveData<List<Season>>
        get() = _seasons
    val selectedSeason: Season?
        get() = _selectedSeason
    val selectedPositionSeason: Int?
        get() = _selectedPositionSeason
    val requestStatus: LiveData<RequestStatus>
        get() = _requestStatus

    fun fetchingApi(): Boolean {
        return  (_requestStatus.value == RequestStatus.Fetching)
    }

    fun getShows() {
        _lastPageIndex++
        _requestStatus.value = RequestStatus.Fetching
        val retrofitClient = NetworkUtils.getRetrofitInstance()
        val endpoint = retrofitClient.create(Api::class.java)
        job = CoroutineScope(Dispatchers.IO).launch {
            val showList = endpoint.getShowsByPage(_lastPageIndex)
            showList.enqueue(object : retrofit2.Callback<List<Show>> {
                override fun onResponse(call: Call<List<Show>>, response: Response<List<Show>>) {
                    response.body()?.let {
                        _shows.value = it
                        _requestStatus.value = RequestStatus.Done
                    }
                }
                override fun onFailure(call: Call<List<Show>>, t: Throwable) {
                    _requestStatus.value = RequestStatus.Error
                }
            })
        }
    }

    private fun getEpisodesByShow(show: Int) {
        _requestStatus.value = RequestStatus.Fetching
        val retrofitClient = NetworkUtils.getRetrofitInstance()
        val endpoint = retrofitClient.create(Api::class.java)
        job = CoroutineScope(Dispatchers.IO).launch {
            val episodeList = endpoint.getEpisodesByShow(show)
            episodeList.enqueue(object : retrofit2.Callback<List<Episode>> {
                override fun onResponse(call: Call<List<Episode>>, response: Response<List<Episode>>) {
                    response.body()?.let {
                        _episodes.value = it
                        _requestStatus.value = RequestStatus.Done
                    }
                }
                override fun onFailure(call: Call<List<Episode>>, t: Throwable) {
                    _requestStatus.value = RequestStatus.Error
                }
            })
        }

    }

    private fun getSeasonsByShow(show: Int) {
        _requestStatus.value = RequestStatus.Fetching
        val retrofitClient = NetworkUtils.getRetrofitInstance()
        val endpoint = retrofitClient.create(Api::class.java)
        job = CoroutineScope(Dispatchers.IO).launch {
            val episodeList = endpoint.getSeasonsByShow(show)
            episodeList.enqueue(object : retrofit2.Callback<List<Season>> {
                override fun onResponse(call: Call<List<Season>>, response: Response<List<Season>>) {
                    response.body()?.let {
                        _seasons.postValue(it)
                        _requestStatus.value = RequestStatus.Done
                    }
                }
                override fun onFailure(call: Call<List<Season>>, t: Throwable) {
                    _requestStatus.value = RequestStatus.Error
                }
            })
        }
    }

    fun getShowByFilter(filter: String) {
        _requestStatus.value = RequestStatus.Fetching
        val retrofitClient = NetworkUtils.getRetrofitInstance()
        val endpoint = retrofitClient.create(Api::class.java)
        job = CoroutineScope(Dispatchers.IO).launch {
            val filteredShowList = endpoint.getShowsByFilter(filter)
            filteredShowList.enqueue(object : retrofit2.Callback<List<ShowFilter>> {
                override fun onResponse(call: Call<List<ShowFilter>>, response: Response<List<ShowFilter>>) {
                    response.body()?.let {
                        var shows: MutableList<Show> = mutableListOf()
                        it.forEach {
                            it.show?.let {
                                shows.add(it)
                            }
                        }
                        _lastPageIndex = -1
                        _shows.value = mutableListOf()
                        _shows.value = shows
                        _shows.notifyObserver()

                        _requestStatus.value = RequestStatus.Done
                    }
                }
                override fun onFailure(call: Call<List<ShowFilter>>, t: Throwable) {
                    _requestStatus.value = RequestStatus.Error
                }
            })
        }
    }

    fun isFirstPage(): Boolean {
        return (_lastPageIndex == 0)
    }

    fun clear() {
        _lastPageIndex = -1
        _requestStatus.value = RequestStatus.Idle
        _selectedShow = null
        _shows.value = mutableListOf()
        _episodes.value = mutableListOf()
        _seasons.value = mutableListOf()
        _episodesBySeason.value = mutableListOf()
        _selectedSeason = null
        _selectedPositionSeason = null
    }

    fun selectShow(show: Show) {
        _selectedShow = show
        getSeasonsByShow(show.id.toInt())
        getEpisodesByShow(show.id.toInt())
    }

    fun selectEpisodesBySeason(season: Int) {
        val list: MutableList<Episode> = mutableListOf()
        _episodes.value?.forEach {
            if (it.season == season) {
                list.add(it)
            }
        }
        _episodesBySeason.value = list
    }

    fun selectSeason(season: Season?) {
        _selectedSeason = season
    }

    fun selectPositionSeason(index: Int?) {
        _selectedPositionSeason = index
    }

}

fun <T> MutableLiveData<T>.notifyObserver() {
    this.value = this.value
}