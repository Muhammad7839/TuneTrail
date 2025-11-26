package com.example.tunetrail

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch

/**
 * Composable function that defines the navigation graph for the application.
 * It uses Jetpack Compose Navigation to set up routes for different screens
 * like role selection, login, registration, and home screens for parents and kids.
 */
@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "role_select"
    ) {
        // Screen for selecting user role (Parent or Kid).
        composable("role_select") {
            RoleSelectScreen(navController)
        }

        composable("login_parent") {
            LoginScreen(navController, role = "PARENT")
        }
        composable("login_kid") {
            LoginScreen(navController, role = "KID")
        }
        composable("register_parent") {
            RegisterScreen(navController, role = "PARENT")
        }
        composable("register_kid") {
            RegisterScreen(navController, role = "KID")
        }

        composable("parent_home") {
            ParentHomeScreen(navController)
        }

        composable(
            route = "parent_report/{kidId}",
            arguments = listOf(
                navArgument("kidId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val kidId = backStackEntry.arguments?.getLong("kidId") ?: 0L
            ParentReportScreen(kidId = kidId)
        }

        // Kid's home screen which has its own nested navigation.
        composable("kid_home") {
            KidHomeScreenNav(navController)
        }

        composable(
            route = "game/{level}/{game}",
            arguments = listOf(
                navArgument("level") { type = NavType.IntType },
                navArgument("game") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val level = backStackEntry.arguments?.getInt("level") ?: 1
            val game = backStackEntry.arguments?.getInt("game") ?: 1
            GameScreen(level = level, game = game)
        }
    }
}

/**
 * Composable function for the Login screen.
 * @param navController The NavController for handling navigation events.
 * @param role The role of the user trying to log in ("PARENT" or "KID").
 * This screen provides fields for email and password, a login button, and navigation to the registration screen or back.
 */
@Composable
fun LoginScreen(navController: NavController, role: String) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Login ($role)")

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.padding(top = 12.dp)
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.padding(top = 12.dp)
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                // Basic validation for empty fields.
                if (email.isBlank() || password.isBlank()) {
                    Toast.makeText(ctx, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                scope.launch {
                    val user = com.example.tunetrail.di.AppGraph.userRepo
                        .login(email, password, role)

                    if (user == null) {
                        // Show error for invalid credentials.
                        Toast.makeText(ctx, "Invalid credentials", Toast.LENGTH_SHORT).show()
                    } else {
                        // On successful login, save user session and navigate to the respective home screen.
                        com.example.tunetrail.di.AppGraph.session.set(user.id, user.role)
                        Toast.makeText(ctx, "Welcome, ${user.name}", Toast.LENGTH_SHORT).show()// Clear back stack up to role_select to prevent going back to login/register flow.
                        if (user.role == "PARENT") {
                            navController.navigate("parent_home") {
                                popUpTo("role_select") { inclusive = true }
                            }
                        } else {
                            navController.navigate("kid_home") {
                                popUpTo("role_select") { inclusive = true }
                            }
                        }
                    }
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Sign In")
        }

        // Button to navigate to the registration screen based on the user's role.
        Button(
            onClick = {
                if (role == "PARENT") {
                    navController.navigate("register_parent")
                } else {
                    navController.navigate("register_kid")
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Go to Register")
        }

        // Button to navigate back to the role selection screen.
        Button(
            onClick = { navController.navigate("role_select") },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Back")
        }
    }
}