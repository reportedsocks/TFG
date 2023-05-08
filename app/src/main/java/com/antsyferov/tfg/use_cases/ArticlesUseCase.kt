package com.antsyferov.tfg.use_cases

import android.net.Uri
import com.antsyferov.tfg.ui.models.Article
import com.antsyferov.tfg.ui.models.User
import com.antsyferov.tfg.util.ResultOf
import kotlinx.coroutines.flow.Flow

interface ArticlesUseCase {

    fun getArticles(publicationId: String): Flow<ResultOf<List<Article>>>

    fun getArticlesByUser(userId: String): Flow<ResultOf<List<Article>>>

    fun getArticleByAuthorId(articleId: String, userId: String): Flow<ResultOf<Article>>

    fun getArticleByPublicationId(articleId: String, publicationId: String): Flow<ResultOf<Article>>

    fun getPdfDownloadUrl(articleId: String): Flow<ResultOf<Uri>>

    suspend fun addArticle(publicationId: String, title: String, description: String, characterCount: Int, user: User, uri: Uri): ResultOf<Unit>

}