package com.antsyferov.tfg

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.antsyferov.tfg.ui.models.Article
import com.antsyferov.tfg.ui.models.Publication
import com.antsyferov.tfg.ui.models.User
import com.antsyferov.tfg.use_cases.ArticlesUseCase
import com.antsyferov.tfg.use_cases.PublicationsListUseCase
import com.antsyferov.tfg.util.ResultOf
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import kotlin.collections.List
@HiltViewModel
class MainViewModel @Inject constructor(
    private val publicationsListUseCase: PublicationsListUseCase,
    private val articlesUseCase: ArticlesUseCase
): ViewModel() {

    val fileUriFlow = MutableStateFlow<Uri?>(null)
    fun getPublications(): Flow<ResultOf<List<Publication>>> {
        return publicationsListUseCase.getPublications()
    }

    fun getArticles(publicationId: String): Flow<ResultOf<List<Article>>> {
        return articlesUseCase.getArticles(publicationId)
    }

    suspend fun addArticle(publicationId: String, title: String, user: User, uri: Uri): ResultOf<Unit> {
        return articlesUseCase.addArticle(publicationId, title, user, uri)
    }

}