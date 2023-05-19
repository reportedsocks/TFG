package com.tfg.domain.interfaces

import android.net.Uri
import com.tfg.domain.models.data.Author
import com.tfg.domain.models.data.Customer
import com.tfg.domain.util.ResultOf
import kotlinx.coroutines.flow.Flow

interface UserDataSource {


    fun getUserRole(userId: String): Flow<ResultOf<Int>>

    suspend fun setUserRole(userId: String, role: Int, selectedPublication: String?, selectedArticle1: String?, selectedArticle2: String?, selectedArticle3: String?): ResultOf<Unit>

    fun getAuthor(userId: String): Flow<ResultOf<Author>>

    suspend fun saveAvatar(uri: Uri): ResultOf<Uri>

    suspend fun updateUserNameAndAvatar(name: String?, avatar: Uri?): ResultOf<Unit>

    suspend fun updateUserEmail(email: String): ResultOf<Unit>

    suspend fun addUser(userId: String, name: String, email: String, phone: String, photoUrl: String): ResultOf<Unit>

    fun getCustomers(): Flow<ResultOf<List<Customer>>>

}