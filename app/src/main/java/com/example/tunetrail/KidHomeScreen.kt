package com.example.tunetrail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun KidHomeScreen() {
    // simple vertical menu: Level 1 with three games for now
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Kid Home")
        Spacer(Modifier.height(16.dp))
        // In a moment we’ll pass NavController here. For now, just show text.
    }
}