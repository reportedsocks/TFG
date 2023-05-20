package com.tfg.domain.use_cases

import android.net.Uri
import com.tfg.domain.interfaces.ArticleDataSource
import com.tfg.domain.models.ui.Article
import com.tfg.domain.models.ui.User
import com.tfg.domain.util.ResultOf
import com.tfg.domain.util.transform
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ArticlesUseCaseImpl @Inject constructor(
    private val articleDataSource: ArticleDataSource
): ArticlesUseCase {
    override fun getArticles(publicationId: String): Flow<ResultOf<List<Article>>> {
       return articleDataSource.getArticles(publicationId).map { result ->
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
        return articleDataSource.getArticlesByUser(userId).map { result ->
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
        return articleDataSource.getArticleByAuthorId(articleId, userId).map { result ->
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
        return articleDataSource.getArticleByPublicationId(articleId, publicationId).map { result ->
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

    override fun getPdfDownloadUrl(articleId: String): Flow<ResultOf<Uri>> {
        return articleDataSource.getPdfDownloadUrl(articleId)
    }

    override suspend fun addArticle(
        publicationId: String,
        title: String,
        description: String,
        characterCount: Int,
        user: User,
        uri: Uri
    ): ResultOf<Unit> {

        val resultId = articleDataSource.addArticle(
            publicationId,
            com.tfg.domain.models.data.Article(
                title = title,
                description = description,
                author = user.id ?: "",
                characterCount = characterCount
            )
        )

        //TODO need to delete article if pdf upload fails
        return if (resultId is ResultOf.Success)
            articleDataSource.savePdf(resultId.data, uri)
        else
            resultId.transform {  }

    }

    override suspend fun updatePdf(articleId: String, uri: Uri): ResultOf<Unit> {
        return articleDataSource.updatePdf(articleId, uri)
    }
}