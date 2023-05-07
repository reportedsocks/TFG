package com.antsyferov.tfg.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun AddArticle(
    modifier: Modifier,
    pdfName: String?,
    shouldShowLoader: Boolean,
    onSaveButtonClick: (String) -> Unit,
    onOpenFile: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {

        if (shouldShowLoader) Loader(modifier = Modifier)

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
                    text = "Selected file:",
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth()
                )
                Text(
                    text = pdfName ?: "",
                    maxLines = 1,
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .fillMaxWidth()
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
                enabled = isPDFSelected && !shouldShowLoader && title.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = "Save", modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp))
            }

        }
    }

}