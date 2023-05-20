package com.antsyferov.tfg.ui.composables

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.antsyferov.tfg.R
import com.tfg.domain.models.ui.Article
import com.tfg.domain.models.ui.Author
import com.tfg.domain.util.ResultOf

@Composable
fun ArticleView(
    articleResult: ResultOf<Article>,
    downloadUri: ResultOf<Uri>,
    authorResult: ResultOf<Author>,
    canUpdatePdf: Boolean,
    showSaveButton: Boolean,
    showErrorSnackBar: (Throwable?) -> Unit,
    openBrowser: (Uri) -> Unit,
    openViewer: (Uri, String) -> Unit,
    openReviews: () -> Unit,
    onOpenFile: () -> Unit
) {

    if (articleResult is ResultOf.Loading || downloadUri is ResultOf.Loading || authorResult is ResultOf.Loading) {
        Loader()
    } else if (articleResult is ResultOf.Failure || downloadUri is ResultOf.Failure || authorResult is ResultOf.Failure) {
        showErrorSnackBar.invoke(null)
    } else if (articleResult is ResultOf.Success && downloadUri is ResultOf.Success && authorResult is ResultOf.Success) {
        val article = articleResult.data
        val author = authorResult.data
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_pdf),
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                )
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.h4,
                    maxLines = 2,
                    modifier = Modifier.padding(start = 16.dp)
                )
                
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = author.name,
                    onValueChange = {},
                    enabled = false,
                    label = { Text(text = "Author:")},
                    colors = TextFieldDefaults.textFieldColors(disabledTextColor = MaterialTheme.colors.onBackground, disabledLabelColor = MaterialTheme.colors.onBackground),
                    modifier = Modifier
                        .weight(1f)
                )

                Spacer(modifier = Modifier.width(16.dp))

                if (author.avatar.isNotEmpty()) {
                    AsyncImage(
                        model = author.avatar,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        placeholder = rememberVectorPainter(image = Icons.Filled.AccountBox),
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colors.primary,
                                shape = CircleShape
                            )
                    )
                }
            }

            if (article.description.isNotEmpty()) {
                TextField(
                    value = article.description,
                    onValueChange = {},
                    enabled = false,
                    label = { Text(text = "Description:")},
                    maxLines = 5,
                    colors = TextFieldDefaults.textFieldColors(disabledTextColor = MaterialTheme.colors.onBackground, disabledLabelColor = MaterialTheme.colors.onBackground),
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                )
            }

            TextField(
                value = article.characterCount.toString(),
                onValueChange = {},
                enabled = false,
                label = { Text(text = "Character count:")},
                colors = TextFieldDefaults.textFieldColors(disabledTextColor = MaterialTheme.colors.onBackground, disabledLabelColor = MaterialTheme.colors.onBackground),
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
            )

            TextField(
                value = article.createdAt.toString(),
                onValueChange = {},
                enabled = false,
                label = { Text(text = "Created at:")},
                colors = TextFieldDefaults.textFieldColors(disabledTextColor = MaterialTheme.colors.onBackground, disabledLabelColor = MaterialTheme.colors.onBackground),
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
            )



            Spacer(modifier = Modifier.weight(1f))


            Button(
                onClick = { openReviews.invoke() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "View Reviews",
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { openViewer.invoke(downloadUri.data, article.title.take(20)) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "View PDF",
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            if (canUpdatePdf) {
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onOpenFile.invoke() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (showSaveButton) "Save selected PDF" else "Update PDF",
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { openBrowser.invoke(downloadUri.data) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Download PDF",
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

        }


    }


}