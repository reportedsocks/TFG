package com.antsyferov.tfg.use_cases

import android.net.Uri
import com.antsyferov.tfg.data.DataSource
import com.antsyferov.tfg.ui.models.Author
import com.antsyferov.tfg.ui.models.UserRole
import com.antsyferov.tfg.util.ResultOf
import com.antsyferov.tfg.util.transform
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProfileUseCaseImpl @Inject constructor(
    private val dataSource: DataSource
): ProfileUseCase {


    override suspend fun saveProfileChanges(
        name: String?,
        email: String?,
        uri: Uri?
    ): ResultOf<Unit> {

        var nameResult: ResultOf<Unit> = ResultOf.Success(Unit)
        var emailResult: ResultOf<Unit> = ResultOf.Success(Unit)
        var avatarResult: ResultOf<Uri?> = ResultOf.Success(null)

        if (uri != null) {
            avatarResult = dataSource.saveAvatar(uri)
        }

        val remoteUri = (avatarResult as? ResultOf.Success)?.data

        if (!name.isNullOrEmpty() || remoteUri != null) {
            nameResult = dataSource.updateUserNameAndAvatar(name, remoteUri)
        }

        if (!email.isNullOrEmpty()) {
            emailResult = dataSource.updateUserEmail(email)
        }

        return if (nameResult is ResultOf.Success && emailResult is ResultOf.Success && avatarResult is ResultOf.Success)
            ResultOf.Success(Unit)
        else
            ResultOf.Failure(null)

    }

    override suspend fun addUser(userId: String, name: String, photoUrl: String): ResultOf<Unit> {
        return dataSource.addUser(userId, name, photoUrl)
    }

    override fun getAuthor(userId: String): Flow<ResultOf<Author>> {
        return dataSource.getAuthor(userId).map { result ->
            result.transform {
                Author(name, photoUrl)
            }
        }
    }

    override fun getUserRole(userId: String): Flow<ResultOf<UserRole>> {
        return dataSource.getUserRole(userId).map {result ->
            result.transform {
                when(this) {
                    0 -> UserRole.AUTHOR
                    1 -> UserRole.REVIEWER
                    2 -> UserRole.ADMIN
                    else -> UserRole.AUTHOR
                }
            }
        }
    }


}