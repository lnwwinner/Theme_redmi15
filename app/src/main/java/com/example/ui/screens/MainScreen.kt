package com.example.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.viewmodel.ThemeViewModel

@Composable
fun MainScreen(viewModel: ThemeViewModel) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                NavigationBarItem(
                    icon = { Icon(Icons.Default.ColorLens, contentDescription = "Editor") },
                    label = { Text("Editor") },
                    selected = currentDestination?.hierarchy?.any { it.route == "editor" } == true,
                    onClick = {
                        navController.navigate("editor") {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Generate") },
                    label = { Text("AI Gen") },
                    selected = currentDestination?.hierarchy?.any { it.route == "generate" } == true,
                    onClick = {
                        navController.navigate("generate") {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.ImageSearch, contentDescription = "Analyze") },
                    label = { Text("Analyze") },
                    selected = currentDestination?.hierarchy?.any { it.route == "analyze" } == true,
                    onClick = {
                        navController.navigate("analyze") {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "editor",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("editor") { ThemeEditorScreen(viewModel) }
            composable("generate") { AiGenerationScreen(viewModel) }
            composable("analyze") { AiAnalysisScreen(viewModel) }
        }
    }
}
