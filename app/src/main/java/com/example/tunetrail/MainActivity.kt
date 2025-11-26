package com.example.tunetrail

// Import necessary Android and Jetpack Compose libraries.
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.tunetrail.ui.theme.TuneTrailTheme
import com.example.tunetrail.AppNavHost

/**
 * The main and only activity for this application.
 * It serves as the entry point and sets up the Jetpack Compose content.
 */
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is first created. This is where you should do all of your normal
     * static set up: create views, bind data to lists, etc.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the dependency injection graph for the application.
        com.example.tunetrail.di.AppGraph.init(applicationContext)
        // Enable edge-to-edge display to allow the app to draw under the system bars.
        enableEdgeToEdge()
        // Set the content of the activity to be a Jetpack Compose UI.
        setContent {
            // Apply the app's custom theme.
            TuneTrailTheme {
                // Set up the navigation host, which manages the app's screens (destinations).
                AppNavHost()
            }
        }
    }
}

/**
 * A simple composable function for displaying a greeting text.
 */
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

/**
 * A preview function for the Greeting composable, allowing it to be viewed in Android Studio's design pane.
 */
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TuneTrailTheme {
        Greeting("Android")
    }
}