package com.antsyferov.tfg.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.antsyferov.tfg.R
import com.tfg.domain.models.ui.ArticleRating
import com.tfg.domain.models.ui.Review
import com.tfg.domain.models.ui.ReviewRelevance
import com.tfg.domain.models.ui.User
import com.tfg.domain.models.ui.UserRole
import com.tfg.domain.util.ResultOf

@Composable
fun ReviewsList(
    modifier: Modifier = Modifier,
    customerRes: ResultOf<User?>,
    articleId: String,
    result: ResultOf<List<Review>>,
    showErrorSnackBar: (Throwable?) -> Unit
) {

    when(result) {
        is ResultOf.Success -> {
            if (result.data.isEmpty()) {
                EmptyList(text = stringResource(id = R.string.error_no_reviews))
            } else {

                if (customerRes is ResultOf.Success && customerRes.data != null) {

                    val customer = customerRes.data
                    val customerArticles = listOf(customer?.articleId1, customer?.articleId2, customer?.articleId3)

                    var reviews = result.data

                    if (customer?.role == UserRole.REVIEWER && customerArticles.contains(articleId)) {
                        reviews = reviews.filter { it.reviewAuthorId == customer.id }
                    }

                    LazyColumn(
                        modifier = modifier,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                        state = rememberLazyListState()
                    ) {
                        items(
                            items = reviews,
                            key = { item: Review -> item.id }
                        ) {
                            Review(review = it)
                        }
                    }

                }

            }

        }
        is ResultOf.Loading -> Loader()
        is ResultOf.Failure -> showErrorSnackBar.invoke(result.e)
    }

}

@Composable
fun Review(
    review: Review
) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colors.primary,
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)) {

            Text(text = stringResource(id = R.string.review_rating), style = MaterialTheme.typography.h6)
            val rating = ArticleRating.getByNum(review.rating)
            Text(
                text = rating.name,
                color = Color.White,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colors.secondary,
                        shape = RoundedCornerShape(2.dp)
                    )
                    .padding(horizontal = 2.dp)
            )


            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = review.description,
                onValueChange = {},
                enabled = false,
                label = {
                    Text(
                        text = stringResource(id = R.string.review_description),
                        Modifier
                            .background(
                                color = MaterialTheme.colors.background,
                                shape = RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp)
                            )
                            .padding(horizontal = 2.dp)
                    )

                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    disabledBorderColor = MaterialTheme.colors.secondary,
                    disabledLabelColor = Color.White,
                    disabledTextColor = Color.White,
                    backgroundColor = MaterialTheme.colors.background
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = stringResource(id = R.string.review_confidence), style = MaterialTheme.typography.h6)
            val relevance = ReviewRelevance.getByNum(review.relevance)
            Text(
                text = relevance.name,
                color = Color.White,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colors.secondary,
                        shape = RoundedCornerShape(2.dp)
                    )
                    .padding(horizontal = 2.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = review.comment,
                onValueChange = {},
                enabled = false,
                label = {
                    Text(
                        text = stringResource(id = R.string.review_comment),
                        Modifier
                            .background(
                                color = MaterialTheme.colors.background,
                                shape = RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp)
                            )
                            .padding(horizontal = 2.dp)
                    )

                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    disabledBorderColor = MaterialTheme.colors.secondary,
                    disabledLabelColor = Color.White,
                    disabledTextColor = Color.White,
                    backgroundColor = MaterialTheme.colors.background
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
            val date = review.createdAt.toString()
            Text(text = stringResource(id = R.string.review_created_at, date), style = MaterialTheme.typography.caption, color = Color.White)

        }

    }

}