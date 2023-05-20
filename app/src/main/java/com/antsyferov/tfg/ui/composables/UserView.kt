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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.antsyferov.tfg.MainViewModel
import com.tfg.domain.models.ui.Article
import com.tfg.domain.models.ui.Publication
import com.tfg.domain.models.ui.User
import com.tfg.domain.models.ui.UserRole
import com.tfg.domain.util.ResultOf

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UserView(
    viewModel: MainViewModel,
    customerResult: ResultOf<User?>,
    showErrorSnackBar: (Throwable?) -> Unit,
    onSaveButtonClick: (UserRole, String?, String?, String?, String?) -> Unit
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
                        colors = TextFieldDefaults.textFieldColors(
                            disabledTextColor = MaterialTheme.colors.onBackground,
                            disabledLabelColor = MaterialTheme.colors.onBackground
                        ),
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
                        colors = TextFieldDefaults.textFieldColors(
                            disabledTextColor = MaterialTheme.colors.onBackground,
                            disabledLabelColor = MaterialTheme.colors.onBackground
                        ),
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

                val publicationsRes by viewModel.publicationsFlow.collectAsStateWithLifecycle()
                var selectedPublication: Publication? by remember { mutableStateOf(null) }
                var selectedArticle1: Article? by remember { mutableStateOf(null) }
                var selectedArticle2: Article? by remember { mutableStateOf(null) }
                var selectedArticle3: Article? by remember { mutableStateOf(null) }

                if (publicationsRes is ResultOf.Success) {
                    val publications = (publicationsRes as ResultOf.Success).data
                    publications.find { customer.publicationId == it.id }?.let {
                        selectedPublication = it
                    }
                    var expandedPublication by remember { mutableStateOf(false) }
                    var selectedPublicationTitle by remember { mutableStateOf(selectedPublication?.title ?: "") }


                    Spacer(modifier = Modifier.height(16.dp))

                    Box(modifier = Modifier.fillMaxWidth()) {
                        ExposedDropdownMenuBox(
                            expanded = expandedPublication,
                            onExpandedChange = {
                                expandedPublication = !expandedPublication
                            }
                        ) {
                            OutlinedTextField(
                                value = selectedPublicationTitle,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(text = "Available publication") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPublication) },
                                modifier = Modifier
                                    .fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = expandedPublication,
                                onDismissRequest = {
                                    expandedPublication = false
                                    focusManager.clearFocus()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(color = MaterialTheme.colors.primary)
                            ) {
                                publications.forEach { item ->
                                    DropdownMenuItem(
                                        onClick = {
                                            selectedPublication = item
                                            selectedPublicationTitle = item.title
                                            expandedPublication = false
                                            focusManager.clearFocus()
                                        }
                                    ) {
                                        Text(
                                            text = item.title,
                                            style = MaterialTheme.typography.body1
                                        )
                                    }
                                }
                            }
                        }
                    }


                    if (!selectedPublication?.id.isNullOrEmpty()) {
                        viewModel.getArticles(selectedPublication?.id ?: "")
                        val articlesRes by viewModel.articlesFlow.collectAsStateWithLifecycle()

                        if (articlesRes is ResultOf.Success && customer.role == UserRole.REVIEWER) {
                            val articles = (articlesRes as ResultOf.Success).data

                            var expandedArticle1 by remember { mutableStateOf(false) }
                            var selectedArticleTitle1 by remember { mutableStateOf(selectedArticle1?.title ?: "") }

                            Spacer(modifier = Modifier.height(16.dp))

                            Box(modifier = Modifier.fillMaxWidth()) {
                                ExposedDropdownMenuBox(
                                    expanded = expandedArticle1,
                                    onExpandedChange = {
                                        expandedArticle1 = !expandedArticle1
                                    }
                                ) {
                                    OutlinedTextField(
                                        value = selectedArticleTitle1,
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text(text = "Available article 1") },
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(
                                                expanded = expandedArticle1
                                            )
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    )

                                    ExposedDropdownMenu(
                                        expanded = expandedArticle1,
                                        onDismissRequest = {
                                            expandedArticle1 = false
                                            focusManager.clearFocus()
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(color = MaterialTheme.colors.primary)
                                    ) {
                                        articles.forEach { item ->
                                            DropdownMenuItem(
                                                onClick = {
                                                    selectedArticle1 = item
                                                    selectedArticleTitle1 = item.title
                                                    expandedArticle1 = false
                                                    focusManager.clearFocus()
                                                }
                                            ) {
                                                Text(
                                                    text = item.title,
                                                    style = MaterialTheme.typography.body1
                                                )
                                            }
                                        }
                                    }
                                }

                            }

                            var expandedArticle2 by remember { mutableStateOf(false) }
                            var selectedArticleTitle2 by remember { mutableStateOf(selectedArticle2?.title ?: "") }

                            Spacer(modifier = Modifier.height(16.dp))

                            Box(modifier = Modifier.fillMaxWidth()) {
                                ExposedDropdownMenuBox(
                                    expanded = expandedArticle2,
                                    onExpandedChange = {
                                        expandedArticle2 = !expandedArticle2
                                    }
                                ) {
                                    OutlinedTextField(
                                        value = selectedArticleTitle2,
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text(text = "Available article 2") },
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(
                                                expanded = expandedArticle2
                                            )
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    )

                                    ExposedDropdownMenu(
                                        expanded = expandedArticle2,
                                        onDismissRequest = {
                                            expandedArticle2 = false
                                            focusManager.clearFocus()
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(color = MaterialTheme.colors.primary)
                                    ) {
                                        articles.forEach { item ->
                                            DropdownMenuItem(
                                                onClick = {
                                                    selectedArticle2 = item
                                                    selectedArticleTitle2 = item.title
                                                    expandedArticle2 = false
                                                    focusManager.clearFocus()
                                                }
                                            ) {
                                                Text(
                                                    text = item.title,
                                                    style = MaterialTheme.typography.body1
                                                )
                                            }
                                        }
                                    }
                                }

                            }

                            var expandedArticle3 by remember { mutableStateOf(false) }
                            var selectedArticleTitle3 by remember { mutableStateOf(selectedArticle3?.title ?: "") }

                            Spacer(modifier = Modifier.height(16.dp))

                            Box(modifier = Modifier.fillMaxWidth()) {
                                ExposedDropdownMenuBox(
                                    expanded = expandedArticle3,
                                    onExpandedChange = {
                                        expandedArticle3 = !expandedArticle3
                                    }
                                ) {
                                    OutlinedTextField(
                                        value = selectedArticleTitle3,
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text(text = "Available article 3") },
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(
                                                expanded = expandedArticle3
                                            )
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    )

                                    ExposedDropdownMenu(
                                        expanded = expandedArticle3,
                                        onDismissRequest = {
                                            expandedArticle3 = false
                                            focusManager.clearFocus()
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(color = MaterialTheme.colors.primary)
                                    ) {
                                        articles.forEach { item ->
                                            DropdownMenuItem(
                                                onClick = {
                                                    selectedArticle3 = item
                                                    selectedArticleTitle3 = item.title
                                                    expandedArticle3 = false
                                                    focusManager.clearFocus()
                                                }
                                            ) {
                                                Text(
                                                    text = item.title,
                                                    style = MaterialTheme.typography.body1
                                                )
                                            }
                                        }
                                    }
                                }

                            }

                        }
                    }





                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = {
                            onSaveButtonClick.invoke(
                                selectedItemType,
                                selectedPublication?.id,
                                selectedArticle1?.id,
                                selectedArticle2?.id,
                                selectedArticle3?.id
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Save",
                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp)
                        )
                    }


                }
            }

        }
    }
}
