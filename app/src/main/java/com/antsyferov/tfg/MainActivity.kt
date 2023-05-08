package com.antsyferov.tfg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.antsyferov.tfg.navigation.*
import com.antsyferov.tfg.ui.models.User
import com.antsyferov.tfg.ui.theme.TFGTheme
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var auth: FirebaseAuth
    private val authUI = AuthUI.getInstance()

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        viewModel.fileUriFlow.value = uri
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        val currentUser = auth.currentUser

        if (currentUser != null) {
            viewModel.userFlow.value = User(
                currentUser.uid,
                currentUser.displayName,
                currentUser.email,
                currentUser.phoneNumber,
                currentUser.photoUrl
            )
            initUi()
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
            viewModel.userFlow.value = User(
                user?.uid,
                user?.displayName,
                user?.email,
                user?.phoneNumber,
                user?.photoUrl
            )
            viewModel.addUser(user?.uid ?: "", user?.displayName ?: "", user?.photoUrl?.toString() ?: "")
            initUi()
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
    }

    private fun initUi() {
        setContent {
            TFGTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: ""
                val user by viewModel.userFlow.collectAsStateWithLifecycle()
                val scaffoldState = rememberScaffoldState()
                val coroutineScope = rememberCoroutineScope()

                Scaffold(
                    scaffoldState = scaffoldState,
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
                            } else null,
                            actions = {
                                if (currentRoute == Screen.Profile.route) {
                                    IconButton(onClick = { navController.navigate(Screen.EditProfile.getNavDirection()) }) {
                                        Icon(
                                            imageVector = Icons.Filled.Edit,
                                            contentDescription = "Edit"
                                        )
                                    }
                                }
                            }
                        )
                    },
                    bottomBar = {
                        BottomNavigation {
                            homeScreens.forEach { screen ->
                                BottomNavigationItem(
                                    icon = { Icon(iconMap[screen] ?: Icons.Filled.List, contentDescription = getString(screen.title)) },
                                    label = { Text(stringResource(screen.title)) },
                                    selected = isSubSection(screen, currentRoute),
                                    onClick = {
                                        navController.navigate(screen.getNavDirection()) {
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
                                val publicationId = navController.currentBackStackEntry?.arguments?.getString(Screen.ArticlesList.params.first())
                                navController.navigate(Screen.AddArticle.getNavDirection(publicationId ?: ""))
                            }) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                            }
                        }
                    },
                    floatingActionButtonPosition = FabPosition.End
                ) { innerPadding ->
                    NavigationGraph(
                        this,
                        navController = navController,
                        innerPadding = innerPadding,
                        viewModel = viewModel,
                        coroutineScope = coroutineScope,
                        scaffoldState = scaffoldState,
                        filePickerLauncher = filePickerLauncher,
                        user = user
                    )
                }
            }
        }
    }

}
