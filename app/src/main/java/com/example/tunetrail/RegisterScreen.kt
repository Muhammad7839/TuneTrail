package com.example.tunetrail

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tunetrail.di.AppGraph
import kotlinx.coroutines.launch

/**
 * Composable function for the registration screen.
 * Allows new users to register as either a "PARENT" or a "KID".
 *
 * @param navController The NavController for navigating between screens.
 * @param role The role of the user to be registered ("KID" or "PARENT").
 */
@Composable
fun RegisterScreen(navController: NavController, role: String) {
    // Get current context and coroutine scope
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    // State variables for user input fields
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // State for parent's email, only used if registering a Kid
    var parentEmail by remember { mutableStateOf("") }
    // A boolean flag to easily check if the role is "KID"
    val isKid = role == "KID"

    // UI layout for the registration form
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display title based on the user role
        Text(if (isKid) "Register Kid" else "Register Parent")

        OutlinedTextField(
            value = name, onValueChange = { name = it },
            label = { Text("Name") }, modifier = Modifier.padding(top = 12.dp)
        )
        OutlinedTextField(
            value = email, onValueChange = { email = it },
            label = { Text("Email") }, modifier = Modifier.padding(top = 12.dp)
        )
        OutlinedTextField(
            value = password, onValueChange = { password = it },
            label = { Text("Password") }, modifier = Modifier.padding(top = 12.dp)
        )

        // Conditionally show the Parent Email field if registering a Kid
        if (isKid) {
            OutlinedTextField(
                value = parentEmail, onValueChange = { parentEmail = it },
                label = { Text("Parent Email (to link)") }, modifier = Modifier.padding(top = 12.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            // Basic validation to ensure all required fields are filled
            if (name.isBlank() || email.isBlank() || password.isBlank() || (isKid && parentEmail.isBlank())) {
                Toast.makeText(ctx, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@Button
            }

            // Launch a coroutine to handle the registration process asynchronously
            scope.launch {
                try {
                    // Access user repository and DAO from the AppGraph
                    val repo = AppGraph.userRepo
                    val dao = AppGraph.db.userDao()

                    // Handle registration logic based on the role
                    if (isKid) {
                        // For a kid, first find the parent by their email
                        val parent = dao.findByEmailAndRole(parentEmail, "PARENT")
                        if (parent == null) {
                            // If parent not found, show an error and stop
                            Toast.makeText(ctx, "Parent not found", Toast.LENGTH_SHORT).show()
                            return@launch
                        }
                        // Register the kid and link to the parent's ID
                        val res = repo.registerKid(name, email, password, parent.id)
                        res.onSuccess { kidId ->
                            AppGraph.session.set(kidId, "KID")
                            Toast.makeText(ctx, "Kid registered!", Toast.LENGTH_SHORT).show()
                            navController.navigate("role_select") { popUpTo("role_select") { inclusive = true } }
                        }.onFailure {
                            Toast.makeText(ctx, it.message ?: "Error", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // For a parent, just register them directly
                        val res = repo.registerParent(name, email, password)
                        res.onSuccess { parentId ->
                            AppGraph.session.set(parentId, "PARENT")
                            Toast.makeText(ctx, "Parent registered!", Toast.LENGTH_SHORT).show()
                            navController.navigate("role_select") { popUpTo("role_select") { inclusive = true } }
                        }.onFailure {
                            Toast.makeText(ctx, it.message ?: "Error", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(ctx, e.message ?: "Unexpected error", Toast.LENGTH_SHORT).show()
                }
            }
        }) {
            Text("Register")
        }

        Spacer(Modifier.height(8.dp))

        // Button to go back to the role selection screen
        Button(onClick = { navController.navigate("role_select") }) {
            Text("Back")
        }
    }
}