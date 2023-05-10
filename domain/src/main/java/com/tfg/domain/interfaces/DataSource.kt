package com.tfg.domain.interfaces

import android.net.Uri
import com.tfg.domain.models.data.Article
import com.tfg.domain.models.data.Author
import com.tfg.domain.models.data.Customer
import com.tfg.domain.models.data.Publication
import com.tfg.domain.util.ResultOf
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

    suspend fun addUser(userId: String, name: String, email: String, phone: String, photoUrl: String): ResultOf<Unit>

    fun getCustomers(): Flow<ResultOf<List<Customer>>>

}