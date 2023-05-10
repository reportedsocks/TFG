package com.antsyferov.tfg.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tfg.domain.models.ui.Article
import com.tfg.domain.models.ui.User
import com.tfg.domain.util.ResultOf

@Composable
fun UsersList(
    result: ResultOf<List<User>>,
    showErrorSnackBar: (Throwable?) -> Unit
) {

    when(result) {
        is ResultOf.Success -> {
            if (result.data.isEmpty()) {
                EmptyList(text = "No Users!", modifier = Modifier)
            } else {
                LazyVerticalGrid(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                    state = rememberLazyGridState()
                ) {
                    items(
                        items = result.data,
                        key = { item: User -> item.id ?: "" }
                    ) {
                        Text(text = it.name ?: "")
                    }
                }
            }

        }
        is ResultOf.Loading -> Loader(modifier = Modifier)
        is ResultOf.Failure -> showErrorSnackBar.invoke(result.e)
    }

}