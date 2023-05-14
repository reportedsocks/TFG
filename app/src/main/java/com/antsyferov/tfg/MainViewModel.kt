package com.antsyferov.tfg

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tfg.domain.models.ui.Article
import com.tfg.domain.models.ui.Author
import com.tfg.domain.models.ui.Publication
import com.tfg.domain.models.ui.User
import com.tfg.domain.models.ui.UserRole
import com.antsyferov.tfg.util.ValidationUtils
import com.tfg.domain.models.data.Customer
import com.tfg.domain.models.ui.Review
import com.tfg.domain.use_cases.ArticlesUseCase
import com.tfg.domain.use_cases.ProfileUseCase
import com.tfg.domain.use_cases.PublicationsListUseCase
import com.tfg.domain.use_cases.ReviewsUseCase
import com.tfg.domain.util.ResultOf
import com.tfg.domain.util.transform
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.List
@HiltViewModel
class MainViewModel @Inject constructor(
    private val publicationsListUseCase: PublicationsListUseCase,
    private val articlesUseCase: ArticlesUseCase,
    private val profileUseCase: ProfileUseCase,
    private val reviewsUseCase: ReviewsUseCase
): ViewModel() {

    val fileUriFlow = MutableStateFlow<Uri?>(null)
    val userFlow = MutableStateFlow(User(null, null, null, null, null))

    val publicationsFlow: StateFlow<ResultOf<List<Publication>>> = publicationsListUseCase.getPublications().stateIn(
        scope = viewModelScope,
        initialValue = ResultOf.Loading,
        started = SharingStarted.WhileSubscribed(5000)
    )

    val customersFlow: StateFlow<ResultOf<List<User>>> = profileUseCase.getCustomers().stateIn(
        scope = viewModelScope,
        initialValue = ResultOf.Loading,
        started = SharingStarted.WhileSubscribed(5000)
    )

    fun getCustomerById(userId: String): Flow<ResultOf<User?>> {
        return profileUseCase.getCustomers().map { result ->
            result.transform { find { customer -> customer.id == userId } }
        }
    }

    fun checkIfUserCanPostReview(): Flow<ResultOf<Boolean>> {
        return MutableStateFlow(ResultOf.Success(true))
    }

    fun getUserRole(userId: String): Flow<ResultOf<UserRole>> {
        return profileUseCase.getUserRole(userId)
    }

    fun getReviews(articleId: String): Flow<ResultOf<List<Review>>> {
        return reviewsUseCase.getReviews(articleId)
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

    fun getArticleAuthor(userId: String): Flow<ResultOf<Author>> {
        return profileUseCase.getAuthor(userId)
    }

    fun getPdfDownloadUrl(articleId: String): Flow<ResultOf<Uri>> {
        return articlesUseCase.getPdfDownloadUrl(articleId)
    }

    suspend fun addArticle(publicationId: String, title: String, description: String, characterCount: Int, user: User, uri: Uri): ResultOf<Unit> {
        return articlesUseCase.addArticle(publicationId, title, description, characterCount, user, uri)
    }

    suspend fun addReview(articleId: String, articleAuthorId: String, authorId: String, review: Review): ResultOf<Unit> {
       return reviewsUseCase.addReview(articleId, articleAuthorId, authorId, review)
    }

    fun addUser(user: User) {
        viewModelScope.launch {
            profileUseCase.addUser(user)
        }
    }

    suspend fun setUserRole(userId: String, role: UserRole): ResultOf<Unit> {
        return profileUseCase.setUserRole(userId, role)
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