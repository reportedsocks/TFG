package com.antsyferov.tfg.ui.composables

import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.antsyferov.tfg.R
import com.tfg.domain.models.ui.Article
import com.tfg.domain.models.ui.User
import com.tfg.domain.util.ResultOf

@Composable
fun UsersList(
    result: ResultOf<List<User>>,
    showErrorSnackBar: (Throwable?) -> Unit,
    onUserSelected: (User) -> Unit
) {

    when(result) {
        is ResultOf.Success -> {
            if (result.data.isEmpty()) {
                EmptyList(text = stringResource(id = R.string.error_no_users), modifier = Modifier)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                    state = rememberLazyListState()
                ) {
                    items(
                        items = result.data,
                        key = { item: User -> item.id ?: "" }
                    ) {
                        UserCard(user = it, onClick = onUserSelected)
                    }
                }
            }

        }
        is ResultOf.Loading -> Loader(modifier = Modifier)
        is ResultOf.Failure -> showErrorSnackBar.invoke(result.e)
    }

}

@Composable
fun UserCard(
    user: User,
    onClick: (User) -> Unit
) {

    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colors.primary,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { onClick.invoke(user) })
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {

            val avatarModifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .border(width = 2.dp, color = MaterialTheme.colors.background, shape = CircleShape)

            if (user.avatar != null && user.avatar.toString().isNotEmpty()) {
                AsyncImage(
                    model = user.avatar,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    placeholder = rememberVectorPainter(image = Icons.Filled.AccountBox),
                    modifier = avatarModifier
                )
            } else {
                Image(
                    painter = rememberVectorPainter(image = Icons.Filled.AccountBox),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.primary),
                    modifier = avatarModifier
                )
            }

            Spacer(Modifier.width(16.dp))

            Column() {
                Text(
                    text = user.name ?: user.email ?: user.phoneNumber ?: "",
                    style = MaterialTheme.typography.h5,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth()
                )

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colors.secondary
                ) {
                    Text(
                        text = user.role.name,
                        style = MaterialTheme.typography.caption,
                        maxLines = 1,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }

    
}