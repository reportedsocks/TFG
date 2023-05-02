package com.antsyferov.tfg.use_cases

import android.net.Uri
import com.antsyferov.tfg.ui.models.UserRole
import com.antsyferov.tfg.util.ResultOf
import kotlinx.coroutines.flow.Flow


interface ProfileUseCase {

    suspend fun saveProfileChanges(name: String?, email: String?, uri: Uri?): ResultOf<Unit>

    suspend fun addUser(userId: String): ResultOf<Unit>

    fun getUserRole(userId: String): Flow<ResultOf<UserRole>>
}