package com.bytheway.androidinapppurchasesample.ui.view

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun MapScreen(navController: NavController, paddingValues: PaddingValues) {
    Text(text = "Map", modifier = Modifier.padding(paddingValues))
}