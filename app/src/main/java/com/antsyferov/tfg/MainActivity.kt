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
import com.antsyferov.tfg.models.Publication
import com.antsyferov.tfg.navigation.Screen
import com.antsyferov.tfg.ui.theme.MainViewModel
import com.antsyferov.tfg.ui.theme.TFGTheme
import java.util.*

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                        composable(Screen.Profile.route) { BasicText("Profile") }
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

@Composable
fun PublicationsList(modifier: Modifier, publications: List<Publication>) {
    val list = remember { publications }
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        state = rememberLazyListState()
    ) {
        items(
            items = list,
            key = { item: Publication -> item.id }
        ) {
            Publication(modifier = Modifier, it)
        }
    }
}

@Composable
fun Publication(modifier: Modifier, publication: Publication) {

    var expanded by remember { mutableStateOf(false) }

    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colors.primary,
        modifier = modifier
    ) {

        Column(modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = publication.image),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Column(
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Text(
                        text = publication.title,
                        style = MaterialTheme.typography.h5
                    )
                    Text(
                        text = "Status: ${publication.status}",
                        style = MaterialTheme.typography.subtitle1
                    )
                    Text(
                        text = "Articles: ${publication.articlesCount}",
                        style = MaterialTheme.typography.subtitle1
                    )

                }

                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f))

                IconButton(
                    onClick = { expanded = !expanded }
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }

            }

            if (expanded) {
                Text(
                    text = publication.description,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

        }
        

        
    }
}
