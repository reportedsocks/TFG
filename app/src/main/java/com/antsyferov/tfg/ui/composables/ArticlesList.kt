package com.antsyferov.tfg.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.antsyferov.tfg.R
import com.tfg.domain.models.ui.Article
import com.tfg.domain.models.ui.User
import com.tfg.domain.models.ui.UserRole
import com.tfg.domain.util.ResultOf

@Composable
fun ArticlesList(
    result: ResultOf<List<Article>>,
    customerRes: ResultOf<User?>,
    onNavToArticle: (String, String) -> Unit,
    showErrorSnackBar: (Throwable?) -> Unit,
    isSelectModeAvailable: Boolean,
    onToggleSelect: (String, Boolean) -> Unit
) {

    var isSelectModeActive by remember { mutableStateOf(false) }

    when(result) {
        is ResultOf.Success -> {
            if (result.data.isEmpty()) {
                EmptyList(text = "No articles at the moment!", modifier = Modifier)
            } else {
                if (customerRes is ResultOf.Success && customerRes.data != null) {

                    val customer = customerRes.data

                    var articles = result.data

                    if (customer?.role == UserRole.REVIEWER) {
                        val allowedArticles = listOf(customer.articleId1, customer.articleId2, customer.articleId3)
                        articles = articles.filter { allowedArticles.contains(it.id) || it.authorId == customer.id }
                    }

                    val list = remember { articles.toMutableStateList() }

                    Box(modifier = Modifier.fillMaxSize()) {
                        LazyVerticalGrid(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                            state = rememberLazyGridState()
                        ) {
                            items(
                                items = list,
                                key = { item: Article -> item.id }
                            ) {
                                Article(
                                    modifier = Modifier,
                                    article = it,
                                    onNavToArticle = if (isSelectModeActive) { articleId, _, isSelected ->
                                        val i = list.indexOf(it)
                                        list[i] = it.copy(isSelected = !isSelected)
                                        onToggleSelect.invoke(articleId, !isSelected)
                                    } else  { articleId, authorId, _ ->
                                        onNavToArticle.invoke(articleId, authorId)
                                    }
                                )
                            }
                        }

                        if (isSelectModeAvailable) {
                            Button(
                                onClick = { isSelectModeActive = !isSelectModeActive },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomStart)
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = if (isSelectModeActive) "Done" else "Select Articles",
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }

                    }


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
    onNavToArticle: (String, String, Boolean) -> Unit
) {


    val border = if (article.isSelected)
        BorderStroke(width = 2.dp, color = MaterialTheme.colors.secondary)
    else null

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colors.primary,
        border = border,
        modifier = modifier.clickable {
            onNavToArticle.invoke(article.id, article.authorId, article.isSelected)
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