package com.antsyferov.tfg.navigation

import android.content.ContentResolver
import android.content.Intent
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
import com.tfg.domain.models.ui.User
import com.tfg.domain.util.ResultOf
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.rajat.pdfviewer.PdfViewerActivity
import com.tfg.domain.models.ui.Publication
import com.tfg.domain.models.ui.Review
import com.tfg.domain.models.ui.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
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
                viewModel = viewModel,
                customerResult = customer,
                showErrorSnackBar = { e -> showErrorSnackBar(e) },
                onSaveButtonClick = { role, selectedPublication, selectedArticle1, selectedArticle2, selectedArticle3 ->
                    coroutineScope.launch {
                        viewModel.setUserRole(userId, role, selectedPublication, selectedArticle1, selectedArticle2, selectedArticle3)
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
            val customerRes by viewModel.getCustomerById(user.id ?: "").collectAsStateWithLifecycle(initialValue = ResultOf.Loading)

            PublicationsList(
                modifier = Modifier,
                result = result,
                customerRes = customerRes,
                showErrorSnackBar = { e -> showErrorSnackBar(e) },
                onNavToArticles = { publicationId ->
                    navController.navigate(Screen.ArticlesList.getNavDirection(publicationId))
                }
            )
        }

        composable(Screen.AddPublication.route) {
            var shouldShowLoader by remember { mutableStateOf(false) }
            AddPublication(
                context = activity,
                isLoading = shouldShowLoader,
                onSaveButtonClick = {

                    coroutineScope.launch {
                        shouldShowLoader = true
                        val result = viewModel.addPublication(it)
                        if (result is ResultOf.Success) {
                            navController.popBackStack()
                            scaffoldState.snackbarHostState.showSnackbar(
                                message = "Publication added!"
                            )
                        } else
                            showErrorSnackBar(null)
                        shouldShowLoader = false
                    }

                }
            )
        }

        composable(Screen.MyArticlesList.route) {
            val result by viewModel.getArticlesByUser(user.id ?: "").collectAsStateWithLifecycle(
                initialValue = ResultOf.Loading
            )
            val customerRes by viewModel.getCustomerById(user.id ?: "").collectAsStateWithLifecycle(initialValue = ResultOf.Loading)
            ArticlesList(
                result = result,
                customerRes = customerRes,
                onNavToArticle = { articleId, authorId -> navController.navigate(Screen.ArticleView.getNavDirection(articleId, authorId, Screen.STUB)) },
                showErrorSnackBar = { e -> showErrorSnackBar(e)},
                isSelectModeAvailable = false,
                onToggleSelect = {_, _ -> }
            )
        }

        composable(
            Screen.ArticlesList.route,
            arguments = listOf(navArgument(Screen.ArticlesList.params.first()) { type = NavType.StringType })
        ) { backStackEntry ->
            val publicationId = backStackEntry.arguments?.getString(Screen.ArticlesList.params.first()) ?: ""
            viewModel.getArticles(publicationId)

            val result by viewModel.articlesFlow.collectAsStateWithLifecycle(
                initialValue = ResultOf.Loading
            )

            var isSelectModeAvailable by remember { mutableStateOf(false) }

            viewModel.getPublicationById(publicationId)
            val publicationRes by viewModel.publicationFlow.collectAsStateWithLifecycle()
            val userRoleRes by viewModel.getUserRole(user.id ?: "").collectAsStateWithLifecycle(initialValue = ResultOf.Loading)

            isSelectModeAvailable = publicationRes is ResultOf.Success &&
                    (publicationRes as ResultOf.Success).data.status == Publication.Status.CLOSED &&
                    userRoleRes is ResultOf.Success && (userRoleRes as ResultOf.Success).data == UserRole.ADMIN

            val customerRes by viewModel.getCustomerById(user.id ?: "").collectAsStateWithLifecycle(initialValue = ResultOf.Loading)
            ArticlesList(
                result = result,
                customerRes = customerRes,
                onNavToArticle = { articleId, authorId -> navController.navigate(Screen.ArticleView.getNavDirection(articleId, authorId, publicationId)) },
                showErrorSnackBar = { e -> showErrorSnackBar(e)},
                isSelectModeAvailable = isSelectModeAvailable,
                onToggleSelect = {articleId, selection ->
                    coroutineScope.launch {
                        viewModel.updateArticleSelection(publicationId, articleId, selection)
                    }
                }
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

            viewModel.getPublication(articleId, authorId)
            val publication by viewModel.publicationFlow.collectAsStateWithLifecycle()

            var allowUpdatePdf by remember { mutableStateOf(false) }

            if (publication is ResultOf.Success) {
                allowUpdatePdf =
                    (publication as ResultOf.Success).data.status == com.tfg.domain.models.ui.Publication.Status.FINAL_SUBMIT &&
                    authorId == user.id

            }
            val pdfUri by viewModel.fileUriFlow.collectAsStateWithLifecycle()
            val showSaveButton = pdfUri != null

            ArticleView(
                articleResult = article,
                downloadUri = downloadUri,
                authorResult = author,
                canUpdatePdf = allowUpdatePdf,
                showSaveButton = showSaveButton,
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
                openReviews = { navController.navigate(Screen.ReviewsList.getNavDirection(articleId, authorId))},
                onOpenFile = {
                    if(showSaveButton) {
                        coroutineScope.launch {
                            pdfUri?.let { uri ->
                                viewModel.fileUriFlow.value = null
                                val res = viewModel.updatePdf(articleId, uri)
                                scaffoldState.snackbarHostState.showSnackbar(
                                    message = if (res is ResultOf.Success) "Pdf Updated!" else "Try again please"
                                )
                            }

                        }

                    } else {
                        filePickerLauncher.launch("application/pdf")
                    }

                }
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
            val customerRes by viewModel.getCustomerById(user.id ?: "").collectAsStateWithLifecycle(initialValue = ResultOf.Loading)

            ReviewsList(
                articleId = articleId,
                customerRes = customerRes,
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