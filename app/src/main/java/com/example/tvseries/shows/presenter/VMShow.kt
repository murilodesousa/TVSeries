package com.example.tvseries.shows.presenter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tvseries.app.network.Api
import com.example.tvseries.shows.data.ShowEntity
import com.example.tvseries.app.network.RetrofitUtils
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Response
import com.example.tvseries.episodes.data.EpisodeEntity
import com.example.tvseries.shows.data.SeasonEntity
import com.example.tvseries.shows.data.ShowFilterEntity

class VMShow: ViewModel() {

    enum class RequestStatus { Idle, Fetching, Done, Error }

    private lateinit var job: Job

    private val _shows = MutableLiveData<List<ShowEntity>>(null)
    private val _filteredShows = MutableLiveData<List<ShowEntity>>(null)
    private val _episodes = MutableLiveData<List<EpisodeEntity>>(null)
    private val _episodesBySeason = MutableLiveData<List<EpisodeEntity>>(null)
    private val _seasons = MutableLiveData<List<SeasonEntity>>()
    private var _requestStatus = MutableLiveData(RequestStatus.Idle)
    private var _selectedShowEntity: ShowEntity? = null
    private var _selectedSeasonEntity: SeasonEntity? = null
    private var _selectedPositionSeason: Int? = null
    private var _lastPageIndex: Int = -1
    private var _filtering = MutableLiveData<Boolean>(false)

    val shows: LiveData<List<ShowEntity>?>
        get() = _shows
    val filteredShows: LiveData<List<ShowEntity>?>
        get() = _filteredShows
    val episodesBySeason: LiveData<List<EpisodeEntity>?>
        get() = _episodesBySeason
    val seasons: LiveData<List<SeasonEntity>>
        get() = _seasons
    val selectedSeasonEntity: SeasonEntity?
        get() = _selectedSeasonEntity
    val selectedPositionSeason: Int?
        get() = _selectedPositionSeason
    val requestStatus: LiveData<RequestStatus>
        get() = _requestStatus
    val filtering: Boolean
        get() = (_filtering.value == true)

    fun fetchingApi(): Boolean {
        return  (_requestStatus.value == RequestStatus.Fetching)
    }

    fun getShows() {
        clearFilter()
        _lastPageIndex++
        _requestStatus.value = RequestStatus.Fetching
        val retrofitClient = RetrofitUtils.getRetrofitInstance()
        val endpoint = retrofitClient.create(Api::class.java)
        job = CoroutineScope(Dispatchers.IO).launch {
            val showList = endpoint.getShowsByPage(_lastPageIndex)
            showList.enqueue(object : retrofit2.Callback<List<ShowEntity>> {
                override fun onResponse(call: Call<List<ShowEntity>>, response: Response<List<ShowEntity>>) {
                    response.body()?.let {
                        _shows.value = it
                        _requestStatus.value = RequestStatus.Done
                    }
                }
                override fun onFailure(call: Call<List<ShowEntity>>, t: Throwable) {
                    _requestStatus.value = RequestStatus.Error
                }
            })
        }
    }

    private fun getEpisodesByShow(show: Int) {
        _requestStatus.value = RequestStatus.Fetching
        val retrofitClient = RetrofitUtils.getRetrofitInstance()
        val endpoint = retrofitClient.create(Api::class.java)
        job = CoroutineScope(Dispatchers.IO).launch {
            val episodeList = endpoint.getEpisodesByShow(show)
            episodeList.enqueue(object : retrofit2.Callback<List<EpisodeEntity>> {
                override fun onResponse(call: Call<List<EpisodeEntity>>, response: Response<List<EpisodeEntity>>) {
                    response.body()?.let {
                        _episodes.value = it
                        _requestStatus.value = RequestStatus.Done
                    }
                }
                override fun onFailure(call: Call<List<EpisodeEntity>>, t: Throwable) {
                    _requestStatus.value = RequestStatus.Error
                }
            })
        }

    }

    private fun getSeasonsByShow(show: Int) {
        _requestStatus.value = RequestStatus.Fetching
        val retrofitClient = RetrofitUtils.getRetrofitInstance()
        val endpoint = retrofitClient.create(Api::class.java)
        job = CoroutineScope(Dispatchers.IO).launch {
            val episodeList = endpoint.getSeasonsByShow(show)
            episodeList.enqueue(object : retrofit2.Callback<List<SeasonEntity>> {
                override fun onResponse(call: Call<List<SeasonEntity>>, response: Response<List<SeasonEntity>>) {
                    response.body()?.let {
                        _seasons.postValue(it)
                        _requestStatus.value = RequestStatus.Done
                    }
                }
                override fun onFailure(call: Call<List<SeasonEntity>>, t: Throwable) {
                    _requestStatus.value = RequestStatus.Error
                }
            })
        }
    }

    fun getShowByFilter(filter: String) {
        setFiltering()
        _requestStatus.value = RequestStatus.Fetching
        val retrofitClient = RetrofitUtils.getRetrofitInstance()
        val endpoint = retrofitClient.create(Api::class.java)
        job = CoroutineScope(Dispatchers.IO).launch {
            val filteredShowList = endpoint.getShowsByFilter(filter)
            filteredShowList.enqueue(object : retrofit2.Callback<List<ShowFilterEntity>> {
                override fun onResponse(call: Call<List<ShowFilterEntity>>, response: Response<List<ShowFilterEntity>>) {
                    response.body()?.let {
                        val showEntities: MutableList<ShowEntity> = mutableListOf()
                        it.forEach {
                            showEntities.add(it.show)
                        }
                        _lastPageIndex = -1
                        _filteredShows.postValue(showEntities)
                        _requestStatus.postValue(RequestStatus.Done)
                    }
                }
                override fun onFailure(call: Call<List<ShowFilterEntity>>, t: Throwable) {
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
        _selectedShowEntity = null
        _shows.value = mutableListOf()
        _episodes.value = mutableListOf()
        _seasons.value = mutableListOf()
        _episodesBySeason.value = mutableListOf()
        _selectedSeasonEntity = null
        _selectedPositionSeason = null
        clearFilter()
    }

    fun selectShow(showEntity: ShowEntity) {
        _selectedShowEntity = showEntity
        getSeasonsByShow(showEntity.id.toInt())
        getEpisodesByShow(showEntity.id.toInt())
    }

    fun selectEpisodesBySeason(season: Int) {
        val list: MutableList<EpisodeEntity> = mutableListOf()
        _episodes.value?.forEach {
            if (it.season == season) {
                list.add(it)
            }
        }
        _episodesBySeason.value = list
    }

    fun selectSeason(seasonEntity: SeasonEntity?) {
        _selectedSeasonEntity = seasonEntity
    }

    fun selectPositionSeason(index: Int?) {
        _selectedPositionSeason = index
    }

    fun setFiltering() {
        _filtering.value = true
    }

    private fun clearFilter() {
        _filtering.value = false
        _filteredShows.value = mutableListOf()
    }

}