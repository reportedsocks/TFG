package com.tfg.domain.use_cases

import android.net.Uri
import com.tfg.domain.models.ui.Author
import com.tfg.domain.models.ui.User
import com.tfg.domain.models.ui.UserRole
import com.tfg.domain.util.ResultOf
import kotlinx.coroutines.flow.Flow


interface ProfileUseCase {

    suspend fun saveProfileChanges(name: String?, email: String?, uri: Uri?): ResultOf<Unit>

    suspend fun addUser(user: User): ResultOf<Unit>

    fun getAuthor(userId: String): Flow<ResultOf<Author>>

    fun getUserRole(userId: String): Flow<ResultOf<UserRole>>

    fun getCustomers(): Flow<ResultOf<List<User>>>
}