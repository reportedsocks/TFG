package com.antsyferov.tfg.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddArticle(
    modifier: Modifier,
    onSaveButtonClick: (String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        var title by remember { mutableStateOf("") }

        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { onSaveButtonClick.invoke(title) },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(text = "Add Article", modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp))
        }

    }
}