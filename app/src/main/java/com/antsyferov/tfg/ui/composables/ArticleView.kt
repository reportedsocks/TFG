package com.antsyferov.tfg.ui.composables

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.antsyferov.tfg.ui.models.Article
import com.antsyferov.tfg.util.ResultOf

@Composable
fun ArticleView(
    articleId: String,
    authorId: String,
    result: ResultOf<List<Article>>
) {
    Text(text = "Article View: $articleId, $authorId, $result")
}