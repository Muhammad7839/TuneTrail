package com.example.tunetrail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

/**
 * Composable function for the Kid's Home Screen.
 * This screen displays a list of levels and games that the user can navigate to.
 * @param navController The NavController used for navigation.
 */
@Composable
fun KidHomeScreenNav(
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),   // helps on phone + tablet, portrait + landscape
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) { // Column to arrange elements vertically.

        // Screen Title
        Text("Kid Home")
        Spacer(Modifier.height(16.dp))

        // LEVEL 1
        Text("Level 1")
        Spacer(Modifier.height(8.dp))
        Button(onClick = { navController.navigate("game/1/1") }) {
            // Navigates to Game 1 of Level 1
            Text("Game 1")
        }
        Spacer(Modifier.height(6.dp))
        Button(onClick = { navController.navigate("game/1/2") }) {
            // Navigates to Game 2 of Level 1
            Text("Game 2")
        }
        Spacer(Modifier.height(6.dp))
        Button(onClick = { navController.navigate("game/1/3") }) {
            Text("Game 3")
            // Navigates to Game 3 of Level 1
        }

        Spacer(Modifier.height(20.dp))

        // LEVEL 2
        Text("Level 2")
        Spacer(Modifier.height(8.dp))
        Button(onClick = { navController.navigate("game/2/1") }) {
            // Navigates to Game 1 of Level 2
            Text("Game 1")
        }
        Spacer(Modifier.height(6.dp))
        Button(onClick = { navController.navigate("game/2/2") }) {
            // Navigates to Game 2 of Level 2
            Text("Game 2")
        }
        Spacer(Modifier.height(6.dp))
        Button(onClick = { navController.navigate("game/2/3") }) {
            Text("Game 3")
            // Navigates to Game 3 of Level 2
        }

        Spacer(Modifier.height(20.dp))

        // LEVEL 3
        Text("Level 3")
        Spacer(Modifier.height(8.dp))
        Button(onClick = { navController.navigate("game/3/1") }) {
            // Navigates to Game 1 of Level 3
            Text("Game 1")
        }
        Spacer(Modifier.height(6.dp))
        Button(onClick = { navController.navigate("game/3/2") }) {
            // Navigates to Game 2 of Level 3
            Text("Game 2")
        }
        Spacer(Modifier.height(6.dp))
        Button(onClick = { navController.navigate("game/3/3") }) {
            Text("Game 3")
            // Navigates to Game 3 of Level 3
        }

        Spacer(Modifier.height(20.dp))

        // LEVEL 4
        Text("Level 4")
        Spacer(Modifier.height(8.dp))
        Button(onClick = { navController.navigate("game/4/1") }) {
            // Navigates to Game 1 of Level 4
            Text("Game 1")
        }
        Spacer(Modifier.height(6.dp))
        Button(onClick = { navController.navigate("game/4/2") }) {
            // Navigates to Game 2 of Level 4
            Text("Game 2")
        }
        Spacer(Modifier.height(6.dp))
        Button(onClick = { navController.navigate("game/4/3") }) {
            Text("Game 3")
            // Navigates to Game 3 of Level 4
        }

        Spacer(Modifier.height(32.dp))

        // Log out button
        Button(onClick = {
            // Navigate back to the role selection screen
            navController.navigate("role_select") {
                // Clear the back stack up to the role_select destination
                popUpTo("role_select") { inclusive = true }
            }
        }) {
            Text("Log out")
        }
    }
}