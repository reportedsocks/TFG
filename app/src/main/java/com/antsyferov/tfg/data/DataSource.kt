package com.antsyferov.tfg.data

import android.net.Uri
import com.antsyferov.tfg.data.models.Article
import com.antsyferov.tfg.data.models.Publication
import com.antsyferov.tfg.util.ResultOf
import kotlinx.coroutines.flow.Flow

interface DataSource {

    fun getPublications(): Flow<ResultOf<List<Publication>>>

    fun getArticles(publicationId: String): Flow<ResultOf<List<Article>>>

    suspend fun addArticle(publicationId: String, article: Article): ResultOf<String>

    suspend fun savePdf(articleId: String, uri: Uri): ResultOf<Unit>

    suspend fun saveAvatar(uri: Uri): ResultOf<Uri>

    suspend fun updateUserNameAndAvatar(name: String?, avatar: Uri?): ResultOf<Unit>

    suspend fun updateUserEmail(email: String): ResultOf<Unit>

}