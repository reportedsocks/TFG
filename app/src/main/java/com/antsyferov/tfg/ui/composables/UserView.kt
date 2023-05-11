package com.antsyferov.tfg.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.tfg.domain.models.ui.User
import com.tfg.domain.models.ui.UserRole
import com.tfg.domain.util.ResultOf

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UserView(
    customerResult: ResultOf<User?>,
    showErrorSnackBar: (Throwable?) -> Unit,
    onSaveButtonClick: (String, UserRole) -> Unit
) {

    if (customerResult is ResultOf.Loading) {
        Loader()
    } else if (customerResult is ResultOf.Failure) {
        showErrorSnackBar.invoke(null)
    } else if (customerResult is ResultOf.Success) {
        val customer = customerResult.data
        if (customer != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val avatarModifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colors.background,
                            shape = CircleShape
                        )

                    if (customer.avatar != null && customer.avatar.toString().isNotEmpty()) {
                        AsyncImage(
                            model = customer.avatar,
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

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = customer.name ?: "",
                        style = MaterialTheme.typography.h4,
                        maxLines = 2,
                        modifier = Modifier.padding(start = 16.dp)
                    )

                }

                if (!customer.email.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        value = customer.email ?: "",
                        onValueChange = {},
                        enabled = false,
                        label = { Text(text = "Email:") },
                        maxLines = 5,
                        colors = TextFieldDefaults.textFieldColors(disabledTextColor = MaterialTheme.colors.onBackground, disabledLabelColor = MaterialTheme.colors.onBackground),
                        modifier = Modifier.fillMaxWidth()
                    )
                }


                if (!customer.phoneNumber.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        value = customer.phoneNumber ?: "",
                        onValueChange = {},
                        enabled = false,
                        label = { Text(text = "Phone:") },
                        maxLines = 5,
                        colors = TextFieldDefaults.textFieldColors(disabledTextColor = MaterialTheme.colors.onBackground, disabledLabelColor = MaterialTheme.colors.onBackground),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                val roleTypes = UserRole.values()
                var expanded by remember { mutableStateOf(false) }
                var selectedText by remember { mutableStateOf(customer.role.toString()) }
                var selectedItemType by remember {
                    mutableStateOf(customer.role)
                }

                val focusManager = LocalFocusManager.current

                Spacer(modifier = Modifier.height(16.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = {
                            expanded = !expanded
                        }
                    ) {
                        OutlinedTextField(
                            value = selectedText,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = {
                                expanded = false
                                focusManager.clearFocus()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(color = MaterialTheme.colors.primary)
                        ) {
                            roleTypes.forEach { item ->
                                DropdownMenuItem(
                                    onClick = {
                                        selectedItemType = item
                                        selectedText = item.name
                                        expanded = false
                                        focusManager.clearFocus()
                                    }
                                ) {
                                    Text(text = item.name, style = MaterialTheme.typography.body1)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { onSaveButtonClick.invoke(customer.id ?: "", selectedItemType) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Save", modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp))
                }


            }
        }

    }
}
