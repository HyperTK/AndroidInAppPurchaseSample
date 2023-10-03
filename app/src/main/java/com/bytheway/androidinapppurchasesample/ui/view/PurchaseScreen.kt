package com.bytheway.androidinapppurchasesample.ui.view

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.PurchasesUpdatedListener
import com.bytheway.androidinapppurchasesample.PurchaseHelper
import com.bytheway.androidinapppurchasesample.ui.compose.Center
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList
import java.lang.IllegalStateException

@Composable
fun PurchaseScreen(navController: NavController, paddingValues: PaddingValues) {
    InPurchase()
}

@Composable
fun InPurchase() {
    val context = LocalContext.current

    Center {
        Button(onClick = { startPurchase(context) }) {
            Text(text = "Button")
        }
    }
}

/**
 * 課金処理を開始する
 */
fun startPurchase(context: Context) {
    val activity = context.findActivity()

    val purchaseHelper = PurchaseHelper(activity)
    purchaseHelper.billingSetup()
    //purchaseHelper.makePurchase()
}

/**
 * Activityを取得する拡張関数
 */
fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("no activity")
}

@Composable
@Preview
fun PreviewPurchaseScreen() {
    InPurchase()
}