package com.example.androidinapppurchasesample

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Money
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.androidinapppurchasesample.ui.theme.AndroidInAppPurchaseSampleTheme
import com.example.androidinapppurchasesample.ui.view.ConversationScreen
import com.example.androidinapppurchasesample.ui.view.MapScreen
import com.example.androidinapppurchasesample.ui.view.ProfileScreen
import com.example.androidinapppurchasesample.ui.view.PurchaseScreen

enum class MainScreen(@StringRes val title: Int) {
    Start(title = R.string.app_name),
    Second(title = R.string.screen_second),
}

sealed class Screen(val route: String, @StringRes val resourceId: Int, val icon: ImageVector) {
    object Profile : Screen("profile", R.string.screen_profile, Icons.Filled.Person)
    object Map : Screen("map", R.string.screen_map, Icons.Outlined.Map)
    object Conversation : Screen("list", R.string.screen_list, Icons.Outlined.List)
    object Purchase : Screen("purchase", R.string.screen_purchase, Icons.Outlined.Money)

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppBar(
    currentScreen: MainScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(id = currentScreen.title)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

@Composable
fun MainApp(
    navController: NavHostController = rememberNavController()
) {
    val items = listOf(
        Screen.Profile,
        Screen.Map,
        Screen.Conversation,
        Screen.Purchase
    )

    Scaffold(
        bottomBar = {
            // Get current back stack entry
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = backStackEntry?.destination

            BottomNavigation(
                elevation = 10.dp,
                backgroundColor = Color.LightGray
            ) {
                items.forEach { screen ->
                    BottomNavigationItem(
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = null,
                            )
                        },
//                        label = {
//                            Text(
//                                text = stringResource(screen.resourceId),
//                                style = TextStyle(
//                                    fontSize = 12.sp,
//                                ),
//                                maxLines = 1
//                            )
//                        },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                // ユーザーが項目を選択するときにバックスタックに大量の宛先スタックが構築されるのを避けるために、
                                // グラフの開始宛先までポップアップします。
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // 同じ項目を再選択するときに、同じ宛先の複数のコピーを避ける
                                launchSingleTop = true
                                // 以前に選択した項目を再選択したときに状態を復元します
                                restoreState = true
                            }
                        },
                    )

                }
            }
        }
    ) { innerPadding ->
        ScreenNavigation(navController, innerPadding)
    }
}

@Composable
fun ScreenNavigation(
    navController: NavHostController,
    paddingValues: PaddingValues
) {
    NavHost(
        navController,
        startDestination = Screen.Profile.route,
        Modifier.padding(paddingValues)
    ) {
        composable(Screen.Profile.route) { ProfileScreen(navController, paddingValues) }
        composable(Screen.Map.route) { MapScreen(navController, paddingValues) }
        composable(Screen.Conversation.route) { ConversationScreen(messages = SampleData.conversationSample) }
        composable(Screen.Purchase.route) { PurchaseScreen(navController, paddingValues) }
    }
}

@Preview
@Composable
fun PreviewMainScreen() {
    AndroidInAppPurchaseSampleTheme {
        MainApp()
    }
}