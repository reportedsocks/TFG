package com.antsyferov.tfg.ui.composables

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.antsyferov.tfg.R
import com.tfg.domain.models.ui.Publication
import com.tfg.domain.models.ui.User
import com.tfg.domain.models.ui.UserRole
import com.tfg.domain.util.ResultOf

@Composable
fun PublicationsList(
    modifier: Modifier,
    result: ResultOf<List<Publication>>,
    customerRes: ResultOf<User?>,
    onNavToArticles: (String) -> Unit,
    showErrorSnackBar: (Throwable?) -> Unit
) {

    when(result) {
        is ResultOf.Success -> {
            if (result.data.isEmpty()) {
                EmptyList(text = stringResource(id = R.string.error_no_publications), modifier = Modifier)
            } else {
                if (customerRes is ResultOf.Success && customerRes.data != null) {

                    val customer = customerRes.data

                    var publications = result.data

                    if (customer?.role == UserRole.AUTHOR) {
                        publications = publications.filter { it.id == customer.publicationId }
                    }

                    LazyColumn(
                        modifier = modifier,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                        state = rememberLazyListState()
                    ) {
                        items(
                            items = publications,
                            key = { item: Publication -> item.id }
                        ) {
                            Publication(modifier = Modifier, it, onNavToArticles)
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
                Icon(
                    painter = painterResource(id = com.tfg.domain.R.drawable.ic_feed),
                    contentDescription = null,
                    tint = MaterialTheme.colors.onPrimary,
                    modifier = Modifier
                        .size(60.dp)
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
                        Publication.Status.OPEN -> R.string.publication_status_open
                        Publication.Status.CLOSED -> R.string.publication_status_closed
                        Publication.Status.FINAL_SUBMIT -> R.string.publication_status_submit
                        Publication.Status.IN_REVIEW -> R.string.publication_status_review
                    }
                    Text(
                        text = stringResource(id = status),
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

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Surface(
                    color = MaterialTheme.colors.background,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp)) {

                        TextField(
                            value = publication.reviewDate.toString(),
                            onValueChange = {  },
                            label = { Text(stringResource(id = R.string.add_publication_review)) },
                            enabled = false,
                            maxLines = 1,
                            modifier = Modifier
                                .fillMaxWidth()
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )
                        TextField(
                            value = publication.finalSubmitDate.toString(),
                            onValueChange = {  },
                            label = { Text(stringResource(id = R.string.add_publication_submit)) },
                            enabled = false,
                            maxLines = 1,
                            modifier = Modifier
                                .fillMaxWidth()
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )
                        TextField(
                            value = publication.completionDate.toString(),
                            onValueChange = {  },
                            label = { Text(stringResource(id = R.string.add_publication_completion)) },
                            enabled = false,
                            maxLines = 1,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                }


            }

        }
    }
}



