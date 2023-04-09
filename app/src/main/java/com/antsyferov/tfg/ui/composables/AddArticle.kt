package com.antsyferov.tfg.ui.composables

import android.net.Uri
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
    pdfName: String?,
    onSaveButtonClick: (String) -> Unit,
    onOpenFile: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        var title by remember { mutableStateOf("") }

        val isPDFSelected = !pdfName.isNullOrEmpty()

        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        if (isPDFSelected) {
            Text(
                text = pdfName ?: "",
                modifier = Modifier
                    .padding(vertical = 4.dp, horizontal = 16.dp)
            )
        }

        Button(
            onClick = { onOpenFile.invoke() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(
                text = if (isPDFSelected) "Choose Another" else "Add PDF",
                modifier = Modifier
                    .padding(vertical = 4.dp, horizontal = 16.dp)
            )

        }
        
        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { onSaveButtonClick.invoke(title) },
            enabled = isPDFSelected,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(text = "Save", modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp))
        }

    }
}