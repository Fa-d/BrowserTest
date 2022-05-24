package com.faddy.browsertest.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faddy.browsertest.models.URLData
import com.faddy.browsertest.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject
constructor(private val repository: AppRepository) : ViewModel() {

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
            val response = repository.getHitCountSingleSite(theUrl)
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
}