package com.antsyferov.tfg.use_cases

import android.net.Uri
import com.antsyferov.tfg.data.DataSource
import com.antsyferov.tfg.util.ResultOf
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

        return if (nameResult is ResultOf.Success && emailResult is ResultOf.Success)
            ResultOf.Success(Unit)
        else
            ResultOf.Failure(null)

    }


}