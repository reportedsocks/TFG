package com.antsyferov.tfg.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
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
import com.tfg.domain.models.ui.User
import com.tfg.domain.models.ui.UserRole
import com.tfg.domain.util.ResultOf

@Composable
fun Profile(
    user: User,
    userRole: ResultOf<UserRole>,
    onSignOutCallback: () -> Unit,
    onDeleteAccountCallback: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val avatarModifier = Modifier
            .size(150.dp)
            .clip(CircleShape)
            .border(width = 3.dp, color = MaterialTheme.colors.primary, shape = CircleShape)

        if (user.avatar != null) {
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


        if (!user.name.isNullOrEmpty()) {
            ProfileField(title = stringResource(id = R.string.profile_name), value = user.name ?: "")
        }

        if (!user.email.isNullOrEmpty()) {
            ProfileField(title = stringResource(id = R.string.profile_email), value = user.email ?: "")
        }

        if (!user.phoneNumber.isNullOrEmpty()) {
            ProfileField(title = stringResource(id = R.string.profile_phone), value = user.phoneNumber ?: "")
        }

        if (userRole is ResultOf.Success) {
            ProfileField(title = stringResource(id = R.string.profile_role), value = when(userRole.data) {
                UserRole.AUTHOR -> stringResource(id = R.string.profile_role_author)
                UserRole.REVIEWER -> stringResource(id = R.string.profile_role_reviewer)
                else -> stringResource(id = R.string.profile_role_admin)
            })
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onSignOutCallback,
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Text(text = stringResource(id = R.string.action_logout))
        }

        Button(
            onClick = onDeleteAccountCallback,
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(text = stringResource(id = R.string.action_delete_acc))
        }
    }
}

@Composable
fun ProfileField(title: String, value: String) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.subtitle1)
        Text(text = value, style = MaterialTheme.typography.h5)
        Divider(thickness = 1.dp, color = MaterialTheme.colors.secondary, modifier = Modifier.padding(top = 8.dp))
    }

}