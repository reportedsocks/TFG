package com.antsyferov.tfg.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.antsyferov.tfg.ui.models.User
import com.antsyferov.tfg.ui.models.UserRole
import com.antsyferov.tfg.ui.theme.Purple200
import com.antsyferov.tfg.util.ResultOf

@Composable
fun Profile(
    user: User,
    userRole: ResultOf<UserRole>,
    onSignOutCallback: () -> Unit,
    onDeleteAccountCallback: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
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
            ProfileField(title = "Name:" , value = user.name)
        }

        if (!user.email.isNullOrEmpty()) {
            ProfileField(title = "Email:" , value = user.email)
        }

        if (!user.phoneNumber.isNullOrEmpty()) {
            ProfileField(title = "Phone:" , value = user.phoneNumber)
        }

        if (userRole is ResultOf.Success) {
            ProfileField(title = "Role:" , value = when(userRole.data) {
                UserRole.AUTHOR -> "Author"
                UserRole.REVIEWER -> "Reviewer"
                else -> "Admin"
            })
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onSignOutCallback,
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Text(text = "Log Out")
        }

        Button(
            onClick = onDeleteAccountCallback,
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(text = "Delete Account")
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
        Divider(thickness = 1.dp, color = Purple200, modifier = Modifier.padding(top = 8.dp))
    }

}