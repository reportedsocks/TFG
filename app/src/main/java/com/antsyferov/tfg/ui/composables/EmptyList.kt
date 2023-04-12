package com.antsyferov.tfg.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EmptyList(
    text: String,
    modifier: Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            Modifier.size(50.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.h5,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}