package com.antsyferov.tfg.ui.composables

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.antsyferov.tfg.R
import com.antsyferov.tfg.ui.models.Article
import com.antsyferov.tfg.util.ResultOf

@Composable
fun ArticlesList(
    modifier: Modifier,
    result: ResultOf<List<Article>>,
    onNavToArticle: (String) -> Unit,
    showErrorSnackBar: (Throwable?) -> Unit
) {

    when(result) {
        is ResultOf.Success -> {
            LazyVerticalGrid(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                state = rememberLazyGridState()
            ) {
                items(
                    items = result.data,
                    key = { item: Article -> item.id }
                ) {
                    Article(
                        modifier = Modifier,
                        article = it,
                        onNavToArticle = onNavToArticle
                    )
                }
            }
        }
        is ResultOf.Loading -> Loader(modifier = Modifier)
        is ResultOf.Failure -> showErrorSnackBar.invoke(result.e)
    }

}

@Composable
fun Article(
    modifier: Modifier,
    article: Article,
    onNavToArticle: (String) -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colors.primary,
        modifier = modifier.clickable {
           onNavToArticle.invoke(article.id)
        }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_pdf),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
            )
            Text(
                text = article.title,
                style = MaterialTheme.typography.body1,
                modifier = Modifier
                    .padding(top = 16.dp)
            )
        }
    }
}