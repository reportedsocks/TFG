package com.antsyferov.tfg.ui.composables

import android.util.Log
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.antsyferov.tfg.data.FirebaseDataSource
import com.antsyferov.tfg.data.models.Article
import com.antsyferov.tfg.ui.models.Publication

@Composable
fun ArticlesList(
    text: String
) {
    val articles: State<List<Article>> = FirebaseDataSource().getArticles(text).collectAsStateWithLifecycle(
        initialValue = emptyList()
    )
    Log.d("MyLogs", articles.toString())
    Text(text = text)
}