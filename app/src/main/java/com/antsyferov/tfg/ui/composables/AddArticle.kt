package com.antsyferov.tfg.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun AddArticle(
    modifier: Modifier,
    pdfName: String?,
    shouldShowLoader: Boolean,
    onSaveButtonClick: (String, String, Int) -> Unit,
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
            var description by remember { mutableStateOf("") }
            var charCount by remember { mutableStateOf(0) }

            val isPDFSelected = !pdfName.isNullOrEmpty()

            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title *") },
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                maxLines = 5,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )

            TextField(
                value = charCount.toString(),
                onValueChange = { charCount = it.toIntOrNull() ?: 0 },
                label = { Text("Number of characters *") },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )

            if (isPDFSelected) {
                Text(
                    text = "Selected file:",
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                )
                Text(
                    text = pdfName ?: "",
                    maxLines = 1,
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .padding(top = 8.dp)
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
                onClick = { onSaveButtonClick.invoke(title, description, charCount) },
                enabled = isPDFSelected && !shouldShowLoader && title.isNotEmpty() && charCount != 0,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = "Save", modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp))
            }

        }
    }

}