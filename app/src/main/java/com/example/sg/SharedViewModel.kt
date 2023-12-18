package com.example.sg

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sg.common.SingleEventLiveData
import com.example.sg.data.APIResult
import com.example.sg.data.APIResult.Companion.loading
import com.example.sg.data.models.User
import com.example.sg.repository.UserRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.Response
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val repository: UserRepositoryImpl,
) : ViewModel() {

    private val _users = SingleEventLiveData<APIResult<List<User>>>()
    val users: LiveData<APIResult<List<User>>> get() = _users


    private val _createUser = SingleEventLiveData<APIResult<Unit>>()
    val createUser: LiveData<APIResult<Unit>> get() = _createUser

    init {
        viewModelScope.launch {
            getUserList()
        }
    }

    suspend fun getUserList() {
        _users.postValue(loading(null))
        viewModelScope.launch {
            repository.getUsers().collect { users ->
                _users.postValue(users)
            }
        }
    }

    suspend fun createUser(user: User) {
        _createUser.postValue(loading(null))
        viewModelScope.launch {
            repository.createUser(user).collect { res ->
                _createUser.postValue(res)
            }
        }
    }
}