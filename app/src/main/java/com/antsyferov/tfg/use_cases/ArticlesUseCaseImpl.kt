package com.antsyferov.tfg.use_cases

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import com.antsyferov.tfg.data.DataSource
import com.antsyferov.tfg.ui.models.Article
import com.antsyferov.tfg.ui.models.User
import com.antsyferov.tfg.util.ResultOf
import com.antsyferov.tfg.util.transform
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ArticlesUseCaseImpl @Inject constructor(
    private val dataSource: DataSource
): ArticlesUseCase {
    override fun getArticles(publicationId: String): Flow<ResultOf<List<Article>>> {
       return dataSource.getArticles(publicationId).map { result ->
            result.transform {
                map { firebaseArticle ->
                    Article(
                        firebaseArticle.id ?: "",
                        firebaseArticle.title
                    )
                }
            }
        }
    }

    override fun getArticlesByUser(userId: String): Flow<ResultOf<List<Article>>> {
        return dataSource.getArticlesByUser(userId).map { result ->
            result.transform {
                map { firebaseArticle ->
                    Article(
                        firebaseArticle.id ?: "",
                        firebaseArticle.title
                    )
                }
            }
        }
    }

    override suspend fun addArticle(
        publicationId: String,
        title: String,
        user: User,
        uri: Uri
    ): ResultOf<Unit> {

        val resultId = dataSource.addArticle(
            publicationId,
            com.antsyferov.tfg.data.models.Article(
                title = title,
                author = user.id ?: ""
            )
        )

        //TODO need to delete article if pdf upload fails
        return if (resultId is ResultOf.Success)
            dataSource.savePdf(resultId.data, uri)
        else
            resultId.transform {  }

    }
}