package com.antsyferov.tfg

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.antsyferov.tfg.ui.models.Article
import com.antsyferov.tfg.ui.models.Publication
import com.antsyferov.tfg.ui.models.User
import com.antsyferov.tfg.use_cases.ArticlesUseCase
import com.antsyferov.tfg.use_cases.ProfileUseCase
import com.antsyferov.tfg.use_cases.PublicationsListUseCase
import com.antsyferov.tfg.util.ResultOf
import com.antsyferov.tfg.util.ValidationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import kotlin.collections.List
@HiltViewModel
class MainViewModel @Inject constructor(
    private val publicationsListUseCase: PublicationsListUseCase,
    private val articlesUseCase: ArticlesUseCase,
    private val profileUseCase: ProfileUseCase
): ViewModel() {

    val fileUriFlow = MutableStateFlow<Uri?>(null)
    val userFlow = MutableStateFlow(User(null, null, null, null, null))
    fun getPublications(): Flow<ResultOf<List<Publication>>> {
        return publicationsListUseCase.getPublications()
    }

    fun getArticles(publicationId: String): Flow<ResultOf<List<Article>>> {
        return articlesUseCase.getArticles(publicationId)
    }

    suspend fun addArticle(publicationId: String, title: String, user: User, uri: Uri): ResultOf<Unit> {
        return articlesUseCase.addArticle(publicationId, title, user, uri)
    }

    suspend fun saveProfile(user: User, name: String, email: String, uri: Uri?): ResultOf<Unit> {
        return profileUseCase.saveProfileChanges(
            if (name != user.name) name else null,
            if (email != user.email) email else null,
            uri
        )
    }

    fun validateName(name: String): Int? {
        return if (ValidationUtils.NAME_LENGTH_PATTERN.matches(name) &&
                    ValidationUtils.SPECIAL_CHARACTERS_PATTERN.matches(name))
            null else R.string.error_name_validation
    }

    fun validateEmail(email: String): Int? {
        return if (ValidationUtils.EMAIL_PATTERN.matches(email))
            null else R.string.error_email_validation
    }

}