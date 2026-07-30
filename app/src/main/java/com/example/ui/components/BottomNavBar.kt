package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Compress
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.automirrored.outlined.Rule
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Compress
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.automirrored.rounded.Rule
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.viewmodel.BottomTab

@Composable
fun BottomNavBar(
    selectedTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.testTag("bottom_nav_bar")
    ) {
        NavigationBarItem(
            selected = selectedTab == BottomTab.COMPRESS,
            onClick = { onTabSelected(BottomTab.COMPRESS) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == BottomTab.COMPRESS) Icons.Rounded.Compress else Icons.Outlined.Compress,
                    contentDescription = "Compress"
                )
            },
            label = { Text("Compress") },
            modifier = Modifier.testTag("tab_compress")
        )
        NavigationBarItem(
            selected = selectedTab == BottomTab.RULES,
            onClick = { onTabSelected(BottomTab.RULES) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == BottomTab.RULES) Icons.AutoMirrored.Rounded.Rule else Icons.AutoMirrored.Outlined.Rule,
                    contentDescription = "Rules"
                )
            },
            label = { Text("Rules") },
            modifier = Modifier.testTag("tab_rules")
        )
        NavigationBarItem(
            selected = selectedTab == BottomTab.HISTORY,
            onClick = { onTabSelected(BottomTab.HISTORY) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == BottomTab.HISTORY) Icons.Rounded.History else Icons.Outlined.History,
                    contentDescription = "History"
                )
            },
            label = { Text("History") },
            modifier = Modifier.testTag("tab_history")
        )
        NavigationBarItem(
            selected = selectedTab == BottomTab.SETTINGS,
            onClick = { onTabSelected(BottomTab.SETTINGS) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == BottomTab.SETTINGS) Icons.Rounded.Settings else Icons.Outlined.Settings,
                    contentDescription = "Settings"
                )
            },
            label = { Text("Settings") },
            modifier = Modifier.testTag("tab_settings")
        )
    }
}
