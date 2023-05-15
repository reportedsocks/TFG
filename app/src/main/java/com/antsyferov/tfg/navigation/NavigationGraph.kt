package com.antsyferov.tfg.navigation

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.recreate
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.antsyferov.tfg.MainViewModel
import com.antsyferov.tfg.ui.composables.*
import com.tfg.domain.models.ui.User
import com.tfg.domain.util.ResultOf
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.rajat.pdfviewer.PdfViewerActivity
import com.tfg.domain.models.ui.Review
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
        
        composable(Screen.UsersList.route) {

            val customers by viewModel.customersFlow.collectAsStateWithLifecycle()

            UsersList(
                result = customers,
                showErrorSnackBar = { e -> showErrorSnackBar(e) },
                onUserSelected = { user -> navController.navigate(Screen.UserView.getNavDirection(user.id ?: ""))}
            )
        }

        composable(
            Screen.UserView.route,
            arguments = listOf(navArgument(Screen.UserView.params.first()) { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString(Screen.UserView.params.first()) ?: ""
            val customer by viewModel.getCustomerById(userId).collectAsStateWithLifecycle(initialValue = ResultOf.Loading)

            UserView(
                customerResult = customer,
                showErrorSnackBar = { e -> showErrorSnackBar(e) },
                onSaveButtonClick = { id, role ->
                    coroutineScope.launch {
                        viewModel.setUserRole(id, role)
                    }
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Profile.route) {
            val role by viewModel.getUserRole(user.id ?: "").collectAsStateWithLifecycle(initialValue = ResultOf.Loading)
            Profile(
                user,
                role,
                onSignOutCallback = {
                    authUI.signOut(activity)
                        .addOnCompleteListener { recreate(activity) }

                },
                onDeleteAccountCallback = {
                    authUI.delete(activity)
                        .addOnCompleteListener { recreate(activity) }
                }

            )
        }
        composable(Screen.PublicationsList.route) {

            val result by viewModel.publicationsFlow.collectAsStateWithLifecycle()

            PublicationsList(
                modifier = Modifier,
                result = result,
                showErrorSnackBar = { e -> showErrorSnackBar(e) },
                onNavToArticles = { publicationId ->
                    navController.navigate(Screen.ArticlesList.getNavDirection(publicationId))
                }
            )
        }

        composable(Screen.AddPublication.route) {

            Column(modifier = Modifier.fillMaxSize()) {

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { coroutineScope.launch { viewModel.addPublication() } },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Save", modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp))
                }

            }
        }

        composable(Screen.MyArticlesList.route) {
            val result by viewModel.getArticlesByUser(user.id ?: "").collectAsStateWithLifecycle(
                initialValue = ResultOf.Loading
            )
            ArticlesList(
                modifier = Modifier,
                result = result,
                onNavToArticle = { articleId, authorId -> navController.navigate(Screen.ArticleView.getNavDirection(articleId, authorId, Screen.STUB)) },
                showErrorSnackBar = { e -> showErrorSnackBar(e)}
            )
        }

        composable(
            Screen.ArticlesList.route,
            arguments = listOf(navArgument(Screen.ArticlesList.params.first()) { type = NavType.StringType })
        ) { backStackEntry ->
            val publicationId = backStackEntry.arguments?.getString(Screen.ArticlesList.params.first()) ?: ""
            val result by viewModel.getArticles(publicationId).collectAsStateWithLifecycle(
                initialValue = ResultOf.Loading
            )
            ArticlesList(
                modifier = Modifier,
                result = result,
                onNavToArticle = { articleId, authorId -> navController.navigate(Screen.ArticleView.getNavDirection(articleId, authorId, publicationId)) },
                showErrorSnackBar = { e -> showErrorSnackBar(e)}
            )
        }

        composable(
            Screen.AddArticle.route,
            arguments = listOf(navArgument(Screen.AddArticle.params.first()) { type = NavType.StringType })
        ) { backStackEntry ->
            val publicationId = backStackEntry.arguments?.getString(Screen.AddArticle.params.first()) ?: ""
            val uri by viewModel.fileUriFlow.collectAsStateWithLifecycle()
            var shouldShowLoader by remember { mutableStateOf(false) }
            AddArticle(
                modifier = Modifier,
                pdfName = uri?.let { queryName(it, activity.contentResolver) },
                shouldShowLoader = shouldShowLoader,
                onSaveButtonClick = { title, description, characterCount ->
                    coroutineScope.launch {
                        shouldShowLoader = true
                        val result =
                            uri?.let {
                                viewModel.addArticle(publicationId, title, description, characterCount, user, it)
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

        composable(
            Screen.ArticleView.route,
            arguments = listOf(
                navArgument(Screen.ArticleView.params.first()) { type = NavType.StringType },
                navArgument(Screen.ArticleView.params[1]) { type = NavType.StringType },
                navArgument(Screen.ArticleView.params[2]) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val articleId = backStackEntry.arguments?.getString(Screen.ArticleView.params.first()) ?: ""
            val authorId = backStackEntry.arguments?.getString(Screen.ArticleView.params[1]) ?: ""
            val publicationId = backStackEntry.arguments?.getString(Screen.ArticleView.params[2]) ?: ""

            val article by if(publicationId != Screen.STUB)
                viewModel.getArticleFromPublication(articleId, publicationId).collectAsStateWithLifecycle(initialValue = ResultOf.Loading)
            else
                viewModel.getArticle(articleId, authorId).collectAsStateWithLifecycle(initialValue = ResultOf.Loading)

            val downloadUri by viewModel.getPdfDownloadUrl(articleId).collectAsStateWithLifecycle(
                initialValue = ResultOf.Loading
            )

            val author by viewModel.getArticleAuthor(authorId).collectAsStateWithLifecycle(
                initialValue = ResultOf.Loading
            )

            ArticleView(
                articleResult = article,
                downloadUri = downloadUri,
                authorResult = author,
                showErrorSnackBar = { e -> showErrorSnackBar(e) },
                openBrowser = { uri ->
                    val i = Intent(Intent.ACTION_VIEW).apply { data = uri }
                    activity.startActivity(i)
                },
                openViewer = { uri, title ->
                    activity.startActivity(
                        PdfViewerActivity.launchPdfFromUrl(
                            activity,
                            uri.toString(),
                            title,
                            "",
                            enableDownload = false
                        )
                    )
                },
                openReviews = { navController.navigate(Screen.ReviewsList.getNavDirection(articleId, authorId))}
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

        composable(
            Screen.ReviewsList.route,
            arguments = listOf(
                navArgument(Screen.ArticleView.params.first()) { type = NavType.StringType },
                navArgument(Screen.ArticleView.params[1]) { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val articleId = backStackEntry.arguments?.getString(Screen.ArticleView.params.first()) ?: ""
            val authorId = backStackEntry.arguments?.getString(Screen.ArticleView.params[1]) ?: ""

            val reviewsResult by viewModel.getReviews(articleId).collectAsStateWithLifecycle(initialValue = ResultOf.Loading)

            ReviewsList(
                result = reviewsResult,
                showErrorSnackBar = { e -> showErrorSnackBar(e) }
            )

        }

        composable(
            Screen.AddReview.route,
            arguments = listOf(
                navArgument(Screen.ArticleView.params.first()) { type = NavType.StringType },
                navArgument(Screen.ArticleView.params[1]) { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val articleId = backStackEntry.arguments?.getString(Screen.ArticleView.params.first()) ?: ""
            val authorId = backStackEntry.arguments?.getString(Screen.ArticleView.params[1]) ?: ""

            AddReview() { rating, description, relevance, comment ->
                coroutineScope.launch {
                    val result = viewModel.addReview(
                        articleId,
                        authorId,
                        user.id ?: "",
                        Review(
                            rating = rating,
                            description = description,
                            relevance = relevance,
                            comment = comment
                        )
                    )

                    navController.popBackStack()

                    if (result is ResultOf.Success) {
                        scaffoldState.snackbarHostState.showSnackbar(
                            message = "Review added!"
                        )
                    } else if (result is ResultOf.Failure) {
                        showErrorSnackBar(result.e)
                    }

                }
            }
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