package com.example.sg

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.sg.common.SingleEventLiveData
import com.example.sg.repository.SampleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.Response
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SampleViewModel @Inject constructor(
    private val repository: SampleRepository,
) : ViewModel() {

    private val _responseImageUploadTest = SingleEventLiveData<String>()
    val responseImageUploadTest: LiveData<String> = _responseImageUploadTest

    private val _responseImageUpload = SingleEventLiveData<Response>()
    val responseImageUpload: LiveData<Response> = _responseImageUpload


    suspend fun uploadImage(file: File) {
        //val res = repository.uploadImage(file)
        //_responseImageUpload.postValue(res)

        // Test
        _responseImageUploadTest.postValue("")
    }
}