package com.antsyferov.tfg.use_cases

import com.antsyferov.tfg.data.DataSource
import com.antsyferov.tfg.ui.models.Article
import com.antsyferov.tfg.ui.models.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ArticlesUseCaseImpl @Inject constructor(
    private val dataSource: DataSource
): ArticlesUseCase {
    override fun getArticles(publicationId: String): Flow<List<Article>> {
        return dataSource.getArticles(publicationId).map {
            it.map { firebaseArticle ->
                Article(
                    firebaseArticle.id ?: "",
                    firebaseArticle.title
                )
            }
        }
    }

    override fun addArticle(publicationId: String, title: String, user: User) {
        dataSource.addArticle(
            publicationId,
            com.antsyferov.tfg.data.models.Article(
                title = title,
                author = user.id ?: ""
            )
        )
    }
}