package com.antsyferov.tfg

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antsyferov.tfg.ui.models.Article
import com.antsyferov.tfg.ui.models.Publication
import com.antsyferov.tfg.ui.models.User
import com.antsyferov.tfg.ui.models.UserRole
import com.antsyferov.tfg.use_cases.ArticlesUseCase
import com.antsyferov.tfg.use_cases.ProfileUseCase
import com.antsyferov.tfg.use_cases.PublicationsListUseCase
import com.antsyferov.tfg.util.ResultOf
import com.antsyferov.tfg.util.ValidationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
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

    val publicationsFlow: StateFlow<ResultOf<List<Publication>>> = publicationsListUseCase.getPublications().stateIn(
        scope = viewModelScope,
        initialValue = ResultOf.Loading,
        started = SharingStarted.WhileSubscribed(5000)
    )

    fun getUserRole(userId: String): Flow<ResultOf<UserRole>> {
        return profileUseCase.getUserRole(userId)
    }

    fun getArticles(publicationId: String): Flow<ResultOf<List<Article>>> {
        return articlesUseCase.getArticles(publicationId)
    }

    fun getArticle(articleId: String, authorId: String): Flow<ResultOf<Article>> {
        return articlesUseCase.getArticleByAuthorId(articleId, authorId)
    }

    fun getArticleFromPublication(articleId: String, publicationId: String): Flow<ResultOf<Article>> {
        return articlesUseCase.getArticleByPublicationId(articleId, publicationId)
    }

    fun getArticlesByUser(userId: String): Flow<ResultOf<List<Article>>> {
        return articlesUseCase.getArticlesByUser(userId)
    }

    suspend fun addArticle(publicationId: String, title: String, description: String, characterCount: Int, user: User, uri: Uri): ResultOf<Unit> {
        return articlesUseCase.addArticle(publicationId, title, description, characterCount, user, uri)
    }

    fun addUser(userId: String) {
        viewModelScope.launch {
            profileUseCase.addUser(userId)
        }
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