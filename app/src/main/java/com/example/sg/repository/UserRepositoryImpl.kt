package com.example.sg.repository

import android.util.Log
import com.example.sg.data.APIResult
import com.example.sg.data.ApiService
import com.example.sg.data.BaseRemoteDataSource
import com.example.sg.data.models.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(private val service: ApiService): IUserRepository, BaseRemoteDataSource() {

    override fun getUsers(): Flow<APIResult<List<User>>> {
        return flow {
            val users = getResult { service.getUsers() }
            emit(users)
        }.catch { exception ->
            Log.e("UserRepository", "Error fetching users", exception)
        }
    }

    override fun createUser(user: User): Flow<APIResult<Unit>> {
        return flow {
            val result = getResult { service.createUser(user) }
            emit(result)
        }.catch { exception ->
            Log.e("UserRepository", "Error creating user", exception)
        }
    }
}