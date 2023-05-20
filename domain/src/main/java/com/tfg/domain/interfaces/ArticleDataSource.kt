package com.tfg.domain.interfaces

import android.net.Uri
import com.tfg.domain.models.data.Article
import com.tfg.domain.util.ResultOf
import kotlinx.coroutines.flow.Flow

interface ArticleDataSource {

    fun getArticles(publicationId: String): Flow<ResultOf<List<Article>>>

    fun getArticlesByUser(userId: String): Flow<ResultOf<List<Article>>>

    fun getArticleByAuthorId(articleId: String, authorId: String): Flow<ResultOf<Article>>

    fun getArticleByPublicationId(articleId: String, publicationId: String): Flow<ResultOf<Article>>

    fun getPdfDownloadUrl(articleId: String): Flow<ResultOf<Uri>>

    suspend fun addArticle(publicationId: String, article: Article): ResultOf<String>

    suspend fun savePdf(articleId: String, uri: Uri): ResultOf<Unit>

    suspend fun updatePdf(articleId: String, uri: Uri): ResultOf<Unit>

    suspend fun updateArticle(publicationId: String, articleId: String, selection: Boolean): ResultOf<Unit>

}