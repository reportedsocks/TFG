package com.antsyferov.tfg.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Profile(
    onSignOutCallback: () -> Unit,
    onDeleteAccountCallback: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = onSignOutCallback
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