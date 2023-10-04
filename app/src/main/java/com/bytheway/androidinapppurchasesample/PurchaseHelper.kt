package com.bytheway.androidinapppurchasesample

import android.app.Activity
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.AcknowledgePurchaseResponseListener
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.Purchase.PurchaseState
import com.android.billingclient.api.PurchasesResponseListener
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StoreItem(
    val productId: String,
    val type: String,
    val isConsume: Boolean = false
)
data class PurchaseHelper(val activity: Activity) {
    companion object {
        const val TYPE_IN_APP = "inapp"
        const val TYPE_IN_SUBS = "subs"

    }
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private lateinit var billingClient: BillingClient
    private lateinit var productDetails: ProductDetails
    private lateinit var purchase: Purchase

    private val _buyEnabled = MutableStateFlow(false)
    val buyEnabled = _buyEnabled.asStateFlow()

    private val _consumeEnabled = MutableStateFlow(false)
    val consumeEnabled = _consumeEnabled.asStateFlow()

    private val _statusText = MutableStateFlow("Initializing...")
    val statusText = _statusText.asStateFlow()

    private val _selectableItems = MutableStateFlow(mutableListOf<ProductDetails>())
    val selectableItems = _selectableItems.asStateFlow()

    private val _consumeItems = MutableStateFlow(mutableListOf<ProductDetails>())
    val consumeItems = _consumeItems.asStateFlow()

    private val _subscribeItems = MutableStateFlow(mutableListOf<ProductDetails>())
    val subscribeItems = _subscribeItems.asStateFlow()


    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            if (billingResult.responseCode ==
                BillingClient.BillingResponseCode.OK && purchases != null
            ) {
                for (purchase in purchases) {
                    completePurchase(purchase)
                }
            } else if (billingResult.responseCode ==
                BillingClient.BillingResponseCode.USER_CANCELED
            ) {
                _statusText.value = "Purchase Canceled"
            } else {
                _statusText.value = "Purchase Error"
                Log.i("InAppPurchase", billingResult.debugMessage)
            }
        }

    /**
     * 非消費型アイテムの購入後の処理
     */
    private val acknowledgePurchaseResponseListener =
        AcknowledgePurchaseResponseListener { billingResult ->
            if (billingResult.responseCode == BillingResponseCode.OK) {
                _statusText.value = "Purchase NonConsumed"
                _buyEnabled.value = false
                _consumeEnabled.value = false
            }
        }

    /**
     * リストアの処理
     */
    private val purchaseListener =
        PurchasesResponseListener { billingResult, purchases ->
            // 購入実績ありの場合
            if (purchases.isNotEmpty()) {
                purchase = purchases.first()
                _buyEnabled.value = false
                _consumeEnabled.value = true
                _statusText.value = "Previous Purchase Found"
            } else {
                _buyEnabled.value = true
                _consumeEnabled.value = false
            }
        }

    /**
     * アプリ内課金の初期設定
     */
     fun billingSetup(storeItems: List<StoreItem>) {
        billingClient = BillingClient.newBuilder(activity)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                _statusText.value = "Billing Client Connection Lost"
            }

            override fun onBillingSetupFinished(p0: BillingResult) {
                if (p0.responseCode == BillingClient.BillingResponseCode.OK) {
                    _statusText.value = "Billing Client Connected"
                    queryProduct(storeItems)
                } else {
                    _statusText.value = "Billing Client Connection Failure"
                }
            }
        })
    }

    /**
     * 購入可能アイテムを取得する
     * @param storeItems アイテムID
     */
    fun queryProduct(storeItems: List<StoreItem>) {
        val queryProductDetailParams = QueryProductDetailsParams.newBuilder()
        var mutableList = mutableListOf<QueryProductDetailsParams.Product>()
        for (item in storeItems) {
            mutableList.add(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(item.productId)
                    .setProductType(
                        item.type
                    )
                    .build()
            )
        }
        queryProductDetailParams.setProductList(mutableList)

        billingClient.queryProductDetailsAsync(
            queryProductDetailParams.build()
        ) { billingResult, productDetailsList ->
            if (billingResult.responseCode != BillingResponseCode.OK) throw IllegalStateException("Query Error")
            if (productDetailsList.isNotEmpty()) {
                when(productDetailsList[0].productType) {
                    TYPE_IN_APP -> _consumeItems.value = productDetailsList
                    TYPE_IN_SUBS -> _subscribeItems.value = productDetailsList
                }
            } else {
                _statusText.value = "No Matching Products Found"
                _buyEnabled.value = false
                _selectableItems.value = mutableListOf()
            }

        }
    }

    /**
     * 購入フローを起動する
     */
    fun makePurchase(productDetails: ProductDetails, offerToken: String = "") {
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                ImmutableList.of(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .setOfferToken(offerToken)
                        .build()
                )
            )
            .build()

        billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    /**
     * 非消費型アイテムの承認
     */
    private fun nonConsumablePurchase() {
        // 購入済み
        if (purchase.purchaseState === PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                coroutineScope.launch {
                    billingClient.acknowledgePurchase(
                        acknowledgePurchaseParams,
                        acknowledgePurchaseResponseListener
                    )
                }
            }
        }
    }

    /**
     * 消費型アイテムの承認
     */
    fun consumePurchase() {
        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        coroutineScope.launch {
            billingClient.consumeAsync(consumeParams) { billingResult, s ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    _statusText.value = "Purchase Consumed"
                    _buyEnabled.value = true
                    _consumeEnabled.value = false
                }
            }
        }
    }

    /**
     * 購入成功時に実行される
     * @param item 購入した製品
     */
    private fun completePurchase(item: Purchase) {
        purchase = item
        // 非消費型アイテムの承認
        nonConsumablePurchase()

//        if (purchase.purchaseState == PurchaseState.PURCHASED) {
//            _buyEnabled.value = false
//            _consumeEnabled.value = true
//            _statusText.value = "Purchase Completed"
//        }
    }
}