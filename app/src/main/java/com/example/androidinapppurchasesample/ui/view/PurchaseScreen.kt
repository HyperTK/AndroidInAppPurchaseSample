package com.example.androidinapppurchasesample.ui.view

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.androidinapppurchasesample.ui.compose.Center

@Composable
fun PurchaseScreen(navController: NavController, paddingValues: PaddingValues) {
    InPurchase()
}

@Composable
fun InPurchase() {
    Center {
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