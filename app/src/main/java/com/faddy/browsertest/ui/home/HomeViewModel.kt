package com.faddy.browsertest.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
}