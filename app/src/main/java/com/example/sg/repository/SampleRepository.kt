package com.example.sg.repository

import com.example.sg.data.ApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SampleRepository @Inject constructor(private val service: ApiService) {

    suspend fun uploadImage(imageFile: File) : Response {
        val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("imageFile", imageFile.name, requestFile)
        return service.uploadImage(imagePart)
    }
}