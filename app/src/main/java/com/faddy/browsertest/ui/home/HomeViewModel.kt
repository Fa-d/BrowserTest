package com.faddy.browsertest.ui.home

import android.webkit.WebView
import android.widget.FrameLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faddy.browsertest.models.MostVisitedSitesModel
import com.faddy.browsertest.models.NewTabsModel
import com.faddy.browsertest.models.URLData
import com.faddy.browsertest.repository.AppRepository
import com.faddy.browsertest.ui.home.adapters.MostVisitedSitesAdapter
import com.faddy.browsertest.ui.home.adapters.SearchHistoryTextAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject
constructor(private val repository: AppRepository) : ViewModel() {

    val savedTabsInfo: MutableList<NewTabsModel> = mutableListOf<NewTabsModel>()
    lateinit var genericContentFrame: FrameLayout
    lateinit var genericWebView: WebView
    var mostVisitedSitesAdapter = MostVisitedSitesAdapter()
    var searchHistoryTextAdapter = SearchHistoryTextAdapter()
    var newTabsTempList = mutableListOf<String>()
    var searchbarItemDataList = mutableListOf<MostVisitedSitesModel>()

    fun deleteAllAppData(): LiveData<Boolean> {
        val responseBody = MutableLiveData<Boolean>(false)
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.getAllScheduleApps()
            withContext(Dispatchers.Main) {
                responseBody.value = true
            }
        }
        return responseBody
    }

    fun checkIfDataAlreadyExists(theUrl: String): LiveData<Boolean> {
        val responseBody = MutableLiveData<Boolean>(false)
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.checkIfDataAlreadyExists(theUrl)
            withContext(Dispatchers.Main) {
                responseBody.value = true
            }
        }
        return responseBody
    }

    fun getHitCountSingleSite(theUrl: String): LiveData<Int> {
        val responseBody = MutableLiveData<Int>(0)
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.getHitCountSingleSite(theUrl) ?: 0
            withContext(Dispatchers.Main) {
                responseBody.value = response
            }
        }
        return responseBody
    }

    fun incrementHitCount(newCount: Int, theURL: String): LiveData<Boolean> {
        val responseBody = MutableLiveData<Boolean>(false)
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.incrementHitCount(newCount, theURL)
            withContext(Dispatchers.Main) {
                responseBody.value = true
            }
        }
        return responseBody
    }

    fun insertUrlIntoTable(model: URLData): LiveData<Boolean> {
        val responseBody = MutableLiveData<Boolean>(false)
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.insertUrlIntoTable(model)
            withContext(Dispatchers.Main) {
                responseBody.value = true
            }
        }
        return responseBody
    }

    fun isCurrentURLBookmarked(url: String): LiveData<Boolean> {
        val responseBody = MutableLiveData<Boolean>(false)
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.isCurrentURLBookmarked(url)
            withContext(Dispatchers.Main) {
                responseBody.value = true
            }
        }
        return responseBody
    }

    fun getTop9MostVisitedSites(): LiveData<List<URLData>> {
        val responseBody = MutableLiveData<List<URLData>>()
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.getTop9MostVisitedSites()
            withContext(Dispatchers.Main) {
                responseBody.value = response
            }
        }
        return responseBody
    }

    fun setFavionToDB(image: ByteArray, theURL: String): LiveData<Boolean> {
        val responseBody = MutableLiveData<Boolean>(false)
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.setFavionToDB(image, theURL)
            withContext(Dispatchers.Main) {
                responseBody.value = response == 1
            }
        }
        return responseBody
    }

    fun setTitleOfUrl(title: String, theURL: String): LiveData<Boolean> {
        val responseBody = MutableLiveData<Boolean>(false)
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.setTitleOfUrl(title, theURL)
            withContext(Dispatchers.Main) {
                responseBody.value = response == 1
            }
        }
        return responseBody
    }

    fun getAllTitleOfDB(): LiveData<List<String>> {
        val responseBody = MutableLiveData<List<String>>()
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.getAllTitleOfDB()
            withContext(Dispatchers.Main) {
                responseBody.value = response
            }
        }
        return responseBody
    }

    fun getTitleURLImageFromDB(): LiveData<List<MostVisitedSitesModel>> {
        val responseBody = MutableLiveData<List<MostVisitedSitesModel>>()
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.getTitleURLImageFromDB()
            withContext(Dispatchers.Main) {
                responseBody.value = response
            }
        }
        return responseBody
    }

    fun setCurrnetUrlBookmarkerd(url: String): LiveData<Int> {
        val responseBody = MutableLiveData<Int>()
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.setCurrnetUrlBookmarkerd(url)
            withContext(Dispatchers.Main) {
                responseBody.value = response
            }
        }
        return responseBody
    }
}