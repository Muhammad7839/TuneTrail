package com.example.tunetrail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

/**
 * Composable function for the Role Selection Screen.
 * This screen is the entry point of the app where the user (parent or kid)
 * selects their role to proceed to the respective login/registration flow.
 *
 * @param navController The NavController used for navigating to other screens.
 */
@Composable
fun RoleSelectScreen(navController: NavController) {
    // A Column layout is used to arrange its children vertically.
    Column(
        modifier = Modifier
            .fillMaxSize() // The Column will occupy the entire available screen space.
            .padding(20.dp), // Adds padding around the content.
        // Center the content vertically within the column.
        verticalArrangement = Arrangement.Center,
        // Center the content horizontally within the column.
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to Tune Trail!")
        Text("Choose your role to begin", modifier = Modifier.padding(0.dp, 16.dp))

        // Button for Parent Login/Registration.
        // When clicked, it navigates to the 'login_parent' route.
        Button(onClick = { navController.navigate("login_parent") }) {
            Text("Parent Login / Register")
        }

        // Button for Kid Login/Registration.
        // When clicked, it navigates to the 'login_kid' route.
        Button(onClick = { navController.navigate("login_kid") }, modifier = Modifier.padding(top = 16.dp)) {
            Text("Kid Login / Register")
        }
    }
}