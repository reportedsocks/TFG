package com.antsyferov.tfg.ui.composables

import android.content.ContentResolver
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.antsyferov.tfg.R
import com.antsyferov.tfg.ui.models.User

@Composable
fun EditProfile(
    contentResolver: ContentResolver,
    modifier: Modifier,
    user: User,
    uri: Uri?,
    verifyName: (String) -> Int?,
    verifyEmail: (String) -> Int?,
    shouldShowLoader: Boolean,
    onSelectImage: () -> Unit,
    onSaveButtonClicked: (String, String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        
        if (shouldShowLoader) Loader(modifier = Modifier)
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            var name by remember { mutableStateOf(user.name) }
            var email by remember { mutableStateOf(user.email) }

            var nameError: Int? by remember { mutableStateOf(null) }
            var emailError: Int? by remember { mutableStateOf(null) }

            val changesMade = name != user.name || email != user.email || uri != null

            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clickable { onSelectImage.invoke() }
            ) {
                val avatarModifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .border(width = 3.dp, color = MaterialTheme.colors.primary, shape = CircleShape)

                if (uri != null) {
                    contentResolver.openInputStream(uri)?.use {
                        Image(
                            bitmap = BitmapFactory.decodeStream(it).asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = avatarModifier
                        )
                    }
                } else if (user.avatar != null) {
                    AsyncImage(
                        model = user.avatar,
                        contentDescription = null,
                        placeholder = rememberVectorPainter(image = Icons.Filled.AccountBox),
                        contentScale = ContentScale.Crop,
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

                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(50.dp)
                )
            }

            TextField(
                value = name ?: "",
                onValueChange = {
                    name = it
                    nameError = verifyName(it)
                },
                label = { Text("Name") },
                isError = nameError != null,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )

            nameError?.let {
                ErrorLabel(errorRes = it, modifier = Modifier)
            }

            TextField(
                value = email ?: "",
                onValueChange = {
                    email = it
                    emailError = verifyEmail(it)
                },
                label = { Text("Email") },
                isError = emailError != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )

            emailError?.let {
                ErrorLabel(errorRes = it, modifier = Modifier)
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onSaveButtonClicked.invoke(name ?: "", email ?: "") },
                enabled = changesMade && !shouldShowLoader && nameError == null && emailError == null,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = "Save", modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp))
            }

        } 
    }
    
}

@Composable
fun ErrorLabel(
    @StringRes
    errorRes: Int,
    modifier: Modifier
) {
    Text(
        text = stringResource(id = errorRes),
        color = MaterialTheme.colors.error,
        style = MaterialTheme.typography.caption,
        textAlign = TextAlign.Start,
        modifier = modifier.fillMaxWidth()
    )
}