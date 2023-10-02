package com.example.androidinapppurchasesample.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController

@Composable
fun PurchaseScreen(navController: NavController, paddingValues: PaddingValues) {
    InPurchase()
}

@Composable
fun InPurchase() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(onClick = { startPurchase() }) {
            Text(text = "Button")
        }
    }

}

fun startPurchase() {

}

@Composable
@Preview
fun PreviewPurchaseScreen() {
    InPurchase()
}