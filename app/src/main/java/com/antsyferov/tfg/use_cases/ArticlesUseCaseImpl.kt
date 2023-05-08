package com.antsyferov.tfg.use_cases

import android.net.Uri
import com.antsyferov.tfg.data.DataSource
import com.antsyferov.tfg.ui.models.Article
import com.antsyferov.tfg.ui.models.User
import com.antsyferov.tfg.util.ResultOf
import com.antsyferov.tfg.util.transform
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
                        firebaseArticle.title,
                        firebaseArticle.description,
                        firebaseArticle.characterCount,
                        firebaseArticle.author,
                        firebaseArticle.createdAt
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
                        firebaseArticle.title,
                        firebaseArticle.description,
                        firebaseArticle.characterCount,
                        firebaseArticle.author,
                        firebaseArticle.createdAt
                    )
                }
            }
        }
    }

    override fun getArticleByAuthorId(articleId: String, userId: String): Flow<ResultOf<Article>> {
        return dataSource.getArticleByAuthorId(articleId, userId).map { result ->
            result.transform {
                Article(
                    id ?: "",
                    title,
                    description,
                    characterCount,
                    author,
                    createdAt
                )
            }
        }
    }

    override fun getArticleByPublicationId(
        articleId: String,
        publicationId: String
    ): Flow<ResultOf<Article>> {
        return dataSource.getArticleByPublicationId(articleId, publicationId).map { result ->
            result.transform {
                Article(
                    id ?: "",
                    title,
                    description,
                    characterCount,
                    author,
                    createdAt
                )
            }
        }
    }

    override suspend fun addArticle(
        publicationId: String,
        title: String,
        description: String,
        characterCount: Int,
        user: User,
        uri: Uri
    ): ResultOf<Unit> {

        val resultId = dataSource.addArticle(
            publicationId,
            com.antsyferov.tfg.data.models.Article(
                title = title,
                description = description,
                author = user.id ?: "",
                characterCount = characterCount
            )
        )

        //TODO need to delete article if pdf upload fails
        return if (resultId is ResultOf.Success)
            dataSource.savePdf(resultId.data, uri)
        else
            resultId.transform {  }

    }
}