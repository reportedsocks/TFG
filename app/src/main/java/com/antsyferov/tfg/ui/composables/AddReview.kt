package com.antsyferov.tfg.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.antsyferov.tfg.R
import com.tfg.domain.models.ui.ArticleRating
import com.tfg.domain.models.ui.ReviewRelevance

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddReview(
    onSaveButtonClick:(Int, String, Int, String) -> Unit
) {
    val scrollState = rememberScrollState()

    val ratingOptions = ArticleRating.values()
    var ratingExpanded by remember { mutableStateOf(false) }
    var selectedRatingText by remember { mutableStateOf(ArticleRating.ADEQUATE.name) }
    var selectedRating by remember { mutableStateOf(ArticleRating.ADEQUATE) }
    
    var description by remember { mutableStateOf("") }

    val relevanceOptions = ReviewRelevance.values()
    var relevanceExpanded by remember { mutableStateOf(false) }
    var selectedRelevanceText by remember { mutableStateOf(ReviewRelevance.INTERMEDIATE.name) }
    var selectedRelevance by remember { mutableStateOf(ReviewRelevance.INTERMEDIATE) }

    var comment by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .scrollable(scrollState, Orientation.Vertical)
    ) {

        Text(
            text = stringResource(id = R.string.add_review_heading),
            style = MaterialTheme.typography.h5
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = ratingExpanded,
            onExpandedChange = {
                ratingExpanded = !ratingExpanded
            }
        ) {
            OutlinedTextField(
                value = selectedRatingText,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = ratingExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = ratingExpanded,
                onDismissRequest = {
                    ratingExpanded = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colors.primary)
            ) {
                ratingOptions.forEach { item ->
                    DropdownMenuItem(
                        onClick = {
                            selectedRating = item
                            selectedRatingText = item.name
                            ratingExpanded = false
                        }
                    ) {
                        Text(text = item.name, style = MaterialTheme.typography.body1)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = description, 
            onValueChange = { description = it },
            maxLines = 5,
            label = { Text(text = stringResource(id = R.string.add_review_description))},
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = relevanceExpanded,
            onExpandedChange = {
                relevanceExpanded = !relevanceExpanded
            }
        ) {
            OutlinedTextField(
                value = selectedRelevanceText,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = relevanceExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = relevanceExpanded,
                onDismissRequest = {
                    relevanceExpanded = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colors.primary)
            ) {
                relevanceOptions.forEach { item ->
                    DropdownMenuItem(
                        onClick = {
                            selectedRelevance = item
                            selectedRelevanceText = item.name
                            relevanceExpanded = false
                        }
                    ) {
                        Text(text = item.name, style = MaterialTheme.typography.body1)
                    }
                }
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = comment,
            onValueChange = { comment = it },
            maxLines = 5,
            label = { Text(text = stringResource(id = R.string.add_review_comment))},
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { onSaveButtonClick.invoke(selectedRating.rating, description, selectedRelevance.num, comment) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.action_save), modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp))
        }


    }
}