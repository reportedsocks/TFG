package com.antsyferov.tfg.ui.composables

import android.widget.ProgressBar
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.antsyferov.tfg.ui.models.Publication
import com.antsyferov.tfg.util.ResultOf

@Composable
fun PublicationsList(
    modifier: Modifier,
    result: ResultOf<List<Publication>>,
    onNavToArticles: (String) -> Unit,
    showErrorSnackBar: (Throwable?) -> Unit
) {

    when(result) {
        is ResultOf.Success -> {
            LazyColumn(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                state = rememberLazyListState()
            ) {
                items(
                    items = result.data,
                    key = { item: Publication -> item.id }
                ) {
                    Publication(modifier = Modifier, it, onNavToArticles)
                }
            }
        }
        is ResultOf.Loading -> Loader(modifier = Modifier)
        is ResultOf.Failure -> showErrorSnackBar.invoke(result.e)
    }

}

@Composable
fun Publication(modifier: Modifier, publication: Publication, onNavToArticles: (String) -> Unit) {

    var expanded by remember { mutableStateOf(false) }

    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colors.primary,
        modifier = modifier.clickable {
            onNavToArticles.invoke(publication.id)
        }
    ) {

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = publication.image),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Column(
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Text(
                        text = publication.title,
                        style = MaterialTheme.typography.h5
                    )
                    val status = when(publication.status) {
                        Publication.Status.OPEN -> "Open"
                        Publication.Status.CLOSED -> "Closed"
                        Publication.Status.IN_REVIEW -> "In Review"
                    }
                    Text(
                        text = "Status: $status",
                        style = MaterialTheme.typography.subtitle1
                    )
                    Text(
                        text = "Articles: xxx",
                        style = MaterialTheme.typography.subtitle1
                    )

                }

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )

                IconButton(
                    onClick = { expanded = !expanded }
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }

            }

            if (expanded) {
                Text(
                    text = publication.description,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

        }
    }
}



