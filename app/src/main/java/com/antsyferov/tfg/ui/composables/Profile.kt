package com.antsyferov.tfg.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.antsyferov.tfg.ui.models.User
import com.antsyferov.tfg.ui.theme.Purple200

@Composable
fun Profile(
    user: User,
    onSignOutCallback: () -> Unit,
    onDeleteAccountCallback: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (!user.name.isNullOrEmpty()) {
            ProfileField(title = "Name:" , value = user.name)
        }

        if (!user.email.isNullOrEmpty()) {
            ProfileField(title = "Email:" , value = user.email)
        }

        if (!user.phoneNumber.isNullOrEmpty()) {
            ProfileField(title = "Phone:" , value = user.phoneNumber)
        }

        Button(
            onClick = onSignOutCallback,
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Text(text = "Log Out")
        }

        Button(
            onClick = onDeleteAccountCallback
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