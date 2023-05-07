package com.antsyferov.tfg.ui.composables

import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun ArticleView(
    articleId: String
) {
    Text(text = "Article View: $articleId")
}