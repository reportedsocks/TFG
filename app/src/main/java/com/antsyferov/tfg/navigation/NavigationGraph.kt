package com.antsyferov.tfg.navigation

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat.recreate
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.antsyferov.tfg.MainViewModel
import com.antsyferov.tfg.ui.composables.*
import com.antsyferov.tfg.ui.models.User
import com.antsyferov.tfg.util.ResultOf
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun NavigationGraph(
    activity: ComponentActivity,
    navController: NavHostController,
    innerPadding: PaddingValues,
    viewModel: MainViewModel,
    coroutineScope: CoroutineScope,
    scaffoldState: ScaffoldState,
    filePickerLauncher: ActivityResultLauncher<String>,
    user: User,
    authUI: AuthUI = AuthUI.getInstance(),
    auth: FirebaseAuth = Firebase.auth
) {

    fun showErrorSnackBar(e: Throwable?) {
        coroutineScope.launch {
            scaffoldState.snackbarHostState.showSnackbar(
                message = "Some error occurred!"
            )
        }
    }

    NavHost(navController, startDestination = Screen.PublicationsList.route, Modifier.padding(innerPadding)) {
        composable(Screen.Profile.route) { Profile(
            user,
            onSignOutCallback = {
                authUI.signOut(activity)
                    .addOnCompleteListener { recreate(activity) }

            },
            onDeleteAccountCallback = {
                authUI.delete(activity)
                    .addOnCompleteListener { recreate(activity) }
            }

        ) }
        composable(Screen.PublicationsList.route) {

            val result by viewModel.publicationsFlow.collectAsStateWithLifecycle()

            PublicationsList(
                modifier = Modifier,
                result = result,
                showErrorSnackBar = { e -> showErrorSnackBar(e) },
                onNavToArticles = { publicationId ->
                    navController.navigate(Screen.ArticlesList.cleanRoute + publicationId)
                }
            )
        }
        composable(Screen.MyArticlesList.route) {
            val result by viewModel.getArticlesByUser(user.id ?: "").collectAsStateWithLifecycle(
                initialValue = ResultOf.Loading
            )
            ArticlesList(
                modifier = Modifier,
                result = result,
                onNavToArticle = {},
                showErrorSnackBar = { e -> showErrorSnackBar(e)}
            )
        }

        composable(
            Screen.ArticlesList.route,
            arguments = listOf(navArgument(Screen.ArticlesList.param ?: "") { type = NavType.StringType })
        ) { backStackEntry ->
            val publicationId = backStackEntry.arguments?.getString(Screen.ArticlesList.param) ?: ""
            val result by viewModel.getArticles(publicationId).collectAsStateWithLifecycle(
                initialValue = ResultOf.Loading
            )
            ArticlesList(
                modifier = Modifier,
                result = result,
                onNavToArticle = {},
                showErrorSnackBar = { e -> showErrorSnackBar(e)}
            )
        }

        composable(
            Screen.AddArticle.route,
            arguments = listOf(navArgument(Screen.AddArticle.param ?: "") { type = NavType.StringType })
        ) { backStackEntry ->
            val publicationId = backStackEntry.arguments?.getString(Screen.AddArticle.param) ?: ""
            val uri by viewModel.fileUriFlow.collectAsStateWithLifecycle()
            var shouldShowLoader by remember { mutableStateOf(false) }
            AddArticle(
                modifier = Modifier,
                pdfName = uri?.let { queryName(it, activity.contentResolver) },
                shouldShowLoader = shouldShowLoader,
                onSaveButtonClick = { title ->
                    coroutineScope.launch {
                        shouldShowLoader = true
                        val result =
                            uri?.let {
                                viewModel.addArticle(publicationId, title, user, it)
                            }
                        if (result is ResultOf.Success) {
                            viewModel.fileUriFlow.value = null
                            navController.navigateUp()
                            scaffoldState.snackbarHostState.showSnackbar(
                                message = "Article added!"
                            )
                        } else
                            showErrorSnackBar(null)
                        shouldShowLoader = false
                    }
                },
                onOpenFile = { filePickerLauncher.launch("application/pdf") }
            )
        }

        composable(Screen.EditProfile.route) {
            val uri by viewModel.fileUriFlow.collectAsStateWithLifecycle()
            var shouldShowLoader by remember { mutableStateOf(false) }

            EditProfile(
                contentResolver = activity.contentResolver,
                modifier = Modifier,
                user = user,
                uri = uri,
                verifyName = viewModel::validateName,
                verifyEmail = viewModel::validateEmail,
                shouldShowLoader = shouldShowLoader,
                onSelectImage = { filePickerLauncher.launch("image/*") },
                onSaveButtonClicked = { name, email ->
                    coroutineScope.launch {
                        shouldShowLoader = true
                        val result = viewModel.saveProfile(user, name, email, uri)
                        if (result is ResultOf.Success) {
                            navController.navigateUp()
                            val firebaseUser = auth.currentUser
                            viewModel.fileUriFlow.value = null
                            viewModel.userFlow.value = User(
                                firebaseUser?.uid,
                                firebaseUser?.displayName,
                                firebaseUser?.email,
                                firebaseUser?.phoneNumber,
                                firebaseUser?.photoUrl
                            )
                            scaffoldState.snackbarHostState.showSnackbar(
                                message = "Profile updated!"
                            )

                        } else
                            showErrorSnackBar(null)
                        shouldShowLoader = false
                    }
                }
            )
        }
    }
}

private fun queryName(uri: Uri, contentResolver: ContentResolver): String? {
    return contentResolver.query(uri, null, null, null, null)?.use {
        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        it.moveToFirst()
        it.getString(nameIndex)
    }
}