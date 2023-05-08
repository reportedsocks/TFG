package com.antsyferov.tfg.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector
import com.antsyferov.tfg.R
import java.lang.StringBuilder

sealed class Screen(
    private val root: String,
    @StringRes val title: Int,
    vararg val params: String
) {

    companion object {
        const val STUB = "stub"
    }

    val route get() =
        StringBuilder().apply {
            append(root)
            for (p in params) { append("/{$p}") }
        }.toString()

    fun getNavDirection(vararg params: String) =
        StringBuilder().apply {
            append(root)
            for (p in params) { append("/$p") }
        }.toString()

    object Profile : Screen(
        root = "profile",
        title = R.string.profile_title
    )

    object PublicationsList : Screen(
        root = "publicationsList",
        title = R.string.publications_list_title
    )

    object MyArticlesList : Screen(
        root = "myArticles",
        title = R.string.my_articles_list_title
    )

    object ArticlesList : Screen(
        root = "articles",
        title = R.string.articles_list_title,
        params = arrayOf("publication_id")
    )

    object AddArticle: Screen(
        root = "addArticle",
        title = R.string.article_add_title,
        params = arrayOf("publication_id")
    )

    object EditProfile: Screen(
        root = "editProfile",
        title = R.string.edit_profile_title
    )

    object ArticleView: Screen(
        root = "article_view",
        title = R.string.article_view_title,
        params = arrayOf("article_id", "author_id", "publication_id")
    )
}
