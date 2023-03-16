package com.antsyferov.tfg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.antsyferov.tfg.models.User
import com.antsyferov.tfg.navigation.Screen
import com.antsyferov.tfg.ui.composables.Profile
import com.antsyferov.tfg.ui.composables.PublicationsList
import com.antsyferov.tfg.ui.theme.MainViewModel
import com.antsyferov.tfg.ui.theme.TFGTheme
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var auth: FirebaseAuth
    private val authUI = AuthUI.getInstance()

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
                val homeScreens = listOf(Screen.PublicationsList, Screen.MyArticlesList, Screen.Profile)
                Scaffold(
                    topBar = {
                        TopAppBar {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentDestination = navBackStackEntry?.destination
                            val selected = homeScreens.find { it.route == (currentDestination?.route ?: "") }
                            Text(
                                text = stringResource(id = selected?.resourceId ?: R.string.publications_list_title),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    },
                    bottomBar = {
                        BottomNavigation {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentDestination = navBackStackEntry?.destination

                            homeScreens.forEach { screen ->
                                BottomNavigationItem(
                                    icon = { Icon(screen.icon, contentDescription = null) },
                                    label = { Text(stringResource(screen.resourceId)) },
                                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
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
                    }
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
                            val list = remember { viewModel.getPublications() }
                            PublicationsList(modifier = Modifier, list)
                        }
                        composable(Screen.MyArticlesList.route) { BasicText("MyArticles") }
                    }
                }
            }
        }
    }

}

@Composable
fun BasicText(text: String) {
    Text(text = text)
}


