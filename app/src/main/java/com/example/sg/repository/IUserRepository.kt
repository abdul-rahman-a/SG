package com.example.sg.repository

import com.example.sg.data.APIResult
import com.example.sg.data.models.User
import kotlinx.coroutines.flow.Flow

interface IUserRepository {
    fun getUsers(): Flow<APIResult<List<User>>>
    fun createUser(user: User): Flow<APIResult<Unit>>
}