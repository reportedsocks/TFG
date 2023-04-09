package com.antsyferov.tfg.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.antsyferov.tfg.ui.models.User

@Composable
fun EditProfile(
    modifier: Modifier,
    user: User,
    onSaveButtonClicked: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        var name by remember { mutableStateOf(user.name) }
        var email by remember { mutableStateOf(user.email) }


        TextField(
            value = name ?: "",
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = email ?: "",
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { onSaveButtonClicked.invoke() },
            enabled = !name.isNullOrEmpty() && !email.isNullOrEmpty(),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(text = "Save", modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp))
        }

    }
}