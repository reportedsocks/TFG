package com.antsyferov.tfg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.antsyferov.tfg.ui.models.Publication
import com.antsyferov.tfg.ui.models.User
import com.antsyferov.tfg.navigation.Screen
import com.antsyferov.tfg.ui.composables.AddArticle
import com.antsyferov.tfg.ui.composables.Profile
import com.antsyferov.tfg.ui.composables.PublicationsList
import com.antsyferov.tfg.ui.composables.ArticlesList
import com.antsyferov.tfg.ui.models.Article
import com.antsyferov.tfg.ui.theme.TFGTheme
import com.antsyferov.tfg.util.*
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var auth: FirebaseAuth
    private val authUI = AuthUI.getInstance()

    private var selectedPublicationId: String? = null

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        val currentUser = auth.currentUser

        if (currentUser != null) {
            initUi(
                User(
                    currentUser.uid,
                    currentUser.displayName,
                    currentUser.email,
                    currentUser.phoneNumber
                )
            )
        } else {

            val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.PhoneBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build()
            )

            val signInIntent = authUI
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build()
            signInLauncher.launch(signInIntent)
        }


    }


    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            val user = auth.currentUser
            initUi(
                User(
                    user?.uid,
                    user?.displayName,
                    user?.email,
                    user?.phoneNumber
                )
            )
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
    }

    private fun initUi(user: User) {
        setContent {
            TFGTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: ""
                Scaffold(
                    topBar = {
                        TopAppBar (
                            title = {
                                val selected = screens.find { it.route == currentRoute }
                                Text(
                                    text = stringResource(id = selected?.title ?: R.string.publications_list_title),
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            },
                            navigationIcon =
                            if (isNotHomeScreen(currentRoute) && navController.previousBackStackEntry != null) {
                                {
                                    IconButton(onClick = { navController.navigateUp() }) {
                                        Icon(
                                            imageVector = Icons.Filled.ArrowBack,
                                            contentDescription = "Back"
                                        )
                                    }
                                }
                            } else null
                        )
                    },
                    bottomBar = {
                        BottomNavigation {
                            homeScreens.forEach { screen ->
                                BottomNavigationItem(
                                    icon = { Icon(screen.icon ?: Icons.Filled.List, contentDescription = getString(screen.title)) },
                                    label = { Text(stringResource(screen.title)) },
                                    selected = isSubSection(screen, currentRoute),
                                    onClick = {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    },
                    floatingActionButton = {
                        if (Screen.ArticlesList.route == currentRoute) {
                            FloatingActionButton(onClick = {
                                navController.navigate(Screen.AddArticle.cleanRoute + selectedPublicationId)
                            }) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                            }
                        }
                    },
                    floatingActionButtonPosition = FabPosition.End
                ) { innerPadding ->
                    NavHost(navController, startDestination = Screen.PublicationsList.route, Modifier.padding(innerPadding)) {
                        composable(Screen.Profile.route) { Profile(
                            user,
                            onSignOutCallback = {
                                authUI.signOut(this@MainActivity)
                                    .addOnCompleteListener { recreate() }

                            },
                            onDeleteAccountCallback = {
                                authUI.delete(this@MainActivity)
                                    .addOnCompleteListener { recreate() }
                            }

                        ) }
                        composable(Screen.PublicationsList.route) {
                            val result by viewModel.getPublications().collectAsStateWithLifecycle(
                                initialValue = ResultOf.Loading
                            )
                            PublicationsList(modifier = Modifier, result) { publicationId ->
                                selectedPublicationId = publicationId
                                navController.navigate(Screen.ArticlesList.cleanRoute + publicationId)
                            }
                        }
                        composable(Screen.MyArticlesList.route) { Text(text = "My Articles") }

                        composable(
                            Screen.ArticlesList.route,
                            arguments = listOf(navArgument(Screen.ArticlesList.param ?: "") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val publicationId = backStackEntry.arguments?.getString(Screen.ArticlesList.param) ?: ""
                            val result by viewModel.getArticles(publicationId).collectAsStateWithLifecycle(
                                initialValue = ResultOf.Loading
                            )
                            ArticlesList(Modifier, result) {}
                        }

                        composable(
                            Screen.AddArticle.route,
                            arguments = listOf(navArgument(Screen.AddArticle.param ?: "") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val publicationId = backStackEntry.arguments?.getString(Screen.AddArticle.param) ?: ""
                            val coroutineScope = rememberCoroutineScope()
                            AddArticle(modifier = Modifier) { title ->
                                coroutineScope.launch {
                                    val result = viewModel.addArticle(publicationId, title, user)
                                    if (result is ResultOf.Success) {
                                        navController.navigateUp()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}



