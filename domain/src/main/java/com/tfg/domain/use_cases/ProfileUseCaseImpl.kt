package com.tfg.domain.use_cases

import android.net.Uri
import com.tfg.domain.interfaces.UserDataSource
import com.tfg.domain.models.ui.Author
import com.tfg.domain.models.ui.User
import com.tfg.domain.models.ui.UserRole
import com.tfg.domain.util.ResultOf
import com.tfg.domain.util.transform
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProfileUseCaseImpl @Inject constructor(
    private val userDataSource: UserDataSource
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
            avatarResult = userDataSource.saveAvatar(uri)
        }

        val remoteUri = (avatarResult as? ResultOf.Success)?.data

        if (!name.isNullOrEmpty() || remoteUri != null) {
            nameResult = userDataSource.updateUserNameAndAvatar(name, remoteUri)
        }

        if (!email.isNullOrEmpty()) {
            emailResult = userDataSource.updateUserEmail(email)
        }

        return if (nameResult is ResultOf.Success && emailResult is ResultOf.Success && avatarResult is ResultOf.Success)
            ResultOf.Success(Unit)
        else
            ResultOf.Failure(null)

    }

    override suspend fun addUser(user: User): ResultOf<Unit> {
        return userDataSource.addUser(user.id ?: "", user.name ?: "", user.email ?: "", user.phoneNumber ?: "", user.avatar?.toString() ?: "")
    }

    override suspend fun setUserRole(userId: String, role: UserRole): ResultOf<Unit> {
        return userDataSource.setUserRole(userId, role.num)
    }

    override fun getAuthor(userId: String): Flow<ResultOf<Author>> {
        return userDataSource.getAuthor(userId).map { result ->
            result.transform {
                Author(name, photoUrl)
            }
        }
    }

    override fun getUserRole(userId: String): Flow<ResultOf<UserRole>> {
        return userDataSource.getUserRole(userId).map { result ->
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

    override fun getCustomers(): Flow<ResultOf<List<User>>> {
        return userDataSource.getCustomers().map { result ->
            result.transform {
                map { customer -> User(customer.id, customer.name, customer.email, customer.phone, Uri.parse(customer.photoUrl), when(customer.role) {
                    1 -> UserRole.REVIEWER
                    2 -> UserRole.ADMIN
                    else -> UserRole.AUTHOR
                }
                )
                }
            }
        }
    }


}