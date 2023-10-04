package com.bytheway.androidinapppurchasesample.ui.view

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.bytheway.androidinapppurchasesample.ui.compose.Center

@Composable
fun ProfileScreen(navController: NavController, paddingValues: PaddingValues) {
    Center(
        modifier = Modifier.padding(paddingValues)
    ) {
        Text(text = "Profile")
    }
}