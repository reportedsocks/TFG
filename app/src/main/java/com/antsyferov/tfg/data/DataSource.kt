package com.antsyferov.tfg.data

import android.net.Uri
import com.antsyferov.tfg.data.models.Article
import com.antsyferov.tfg.data.models.Author
import com.antsyferov.tfg.data.models.Publication
import com.antsyferov.tfg.util.ResultOf
import kotlinx.coroutines.flow.Flow

interface DataSource {

    fun getPublications(): Flow<ResultOf<List<Publication>>>

    fun getArticles(publicationId: String): Flow<ResultOf<List<Article>>>

    fun getArticlesByUser(userId: String): Flow<ResultOf<List<Article>>>

    fun getArticleByAuthorId(articleId: String, authorId: String): Flow<ResultOf<Article>>

    fun getArticleByPublicationId(articleId: String, publicationId: String): Flow<ResultOf<Article>>

    fun getPdfDownloadUrl(articleId: String): Flow<ResultOf<Uri>>

    fun getUserRole(userId: String): Flow<ResultOf<Int>>

    fun getAuthor(userId: String): Flow<ResultOf<Author>>

    suspend fun addArticle(publicationId: String, article: Article): ResultOf<String>

    suspend fun savePdf(articleId: String, uri: Uri): ResultOf<Unit>

    suspend fun saveAvatar(uri: Uri): ResultOf<Uri>

    suspend fun updateUserNameAndAvatar(name: String?, avatar: Uri?): ResultOf<Unit>

    suspend fun updateUserEmail(email: String): ResultOf<Unit>

    suspend fun addUser(userId: String, name: String, photoUrl: String): ResultOf<Unit>

}