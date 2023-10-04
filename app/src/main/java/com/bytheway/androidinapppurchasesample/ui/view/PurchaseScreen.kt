package com.bytheway.androidinapppurchasesample.ui.view

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.android.billingclient.api.BillingClient
import com.bytheway.androidinapppurchasesample.PurchaseHelper
import com.bytheway.androidinapppurchasesample.R
import com.bytheway.androidinapppurchasesample.StoreItem
import com.bytheway.androidinapppurchasesample.ui.compose.Center

lateinit var purchaseHelper: PurchaseHelper

const val PLAN_TAG_MONTHLY = "monthly"
const val PLAN_TAG_YEAR = "year"

@Composable
fun PurchaseScreen(navController: NavController, paddingValues: PaddingValues) {
    InPurchase()
}

@Composable
fun InPurchase() {
    val context = LocalContext.current
    val activity = context.findActivity()
    // 課金ヘルパークラス
    purchaseHelper = PurchaseHelper(activity)
    val statusText by purchaseHelper.statusText.collectAsState("")
    val products = listOf(
        listOf(
        StoreItem("sample_item_01", BillingClient.ProductType.INAPP),
        StoreItem("sample_item_02", BillingClient.ProductType.INAPP),
        StoreItem("sample_item_03", BillingClient.ProductType.INAPP, true),
        ),
        listOf(
            StoreItem("sample_item_sub_01", BillingClient.ProductType.SUBS)
        )
    )

    products.forEach {
        // 課金セットアップ
        purchaseHelper.billingSetup(it)
    }

    val consumeItems by purchaseHelper.consumeItems.collectAsState(mutableListOf())
    val subscribeItems by purchaseHelper.subscribeItems.collectAsState(mutableListOf())

    Center {
        Text(text = "Status:$statusText")
        // 消費系アイテムの表示
        consumeItems.forEach { item ->
            ElevatedCard(
                elevation=CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
                modifier= Modifier
                    .padding(8.dp)
                    .size(width = 300.dp, height = 150.dp)) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(text = item.productId)
                    OutlinedButton(
                        onClick = { purchaseHelper.makePurchase(item) }) {
                        Text(text = item.name)
                    }
                    Text(text = item.description)
                }
            }
        }
        // サブスクプランの表示
        subscribeItems.forEach { item ->
            if (item.subscriptionOfferDetails.isNullOrEmpty()) return@forEach
            item.subscriptionOfferDetails!!.forEach { offerDetail ->
                Row(
                    modifier = Modifier.padding(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { purchaseHelper.makePurchase(item, offerDetail.offerToken) }) {
                        when(offerDetail.basePlanId) {
                            PLAN_TAG_YEAR -> Text(text = stringResource(R.string.button_plan_year))
                            PLAN_TAG_MONTHLY -> Text(text = stringResource(R.string.button_plan_monthly))
                        }
                    }
                }
            }
        }
    }
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