package com.example.tunetrail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

/**
 * Composable function for the Parent's Home Screen.
 * This screen provides options for parents to view their child's progress report or to log out.
 *
 * @param navController The NavController used for navigating between screens.
 */
@Composable
fun ParentHomeScreen(
    navController: NavController
) {
    // Column layout to arrange UI elements vertically and center them on the screen.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title text for the screen.
        Text("Parent Home")
        Spacer(Modifier.height(16.dp))

        // Button to navigate to the progress report screen for a specific child (hardcoded to kid #1 for now).
        Button(onClick = {
            // Navigates to the "parent_report/{kidId}" route.
            navController.navigate("parent_report/1")
        }) {
            Text("View Progress Report for Kid #1")
        }

        Spacer(Modifier.height(16.dp))

        // Button to log out, which navigates back to the role selection screen.
        Button(onClick = {
            navController.navigate("role_select") {
                popUpTo("role_select") { inclusive = true }
            }
        }) {
            Text("Log out")
        }
    }
}