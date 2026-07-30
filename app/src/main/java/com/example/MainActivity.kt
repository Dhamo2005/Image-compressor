package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.BottomNavBar
import com.example.ui.components.GmailTopBar
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import com.example.ui.screens.AboutDialog
import com.example.ui.screens.CompressScreen
import com.example.ui.screens.HelpTutorialDialog
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.RulesScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.ImageCompressorProTheme
import com.example.viewmodel.BottomTab
import com.example.viewmodel.CompressViewModel
import com.example.viewmodel.HistoryViewModel
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.RulesViewModel
import com.example.viewmodel.SettingsViewModel
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private val compressViewModel: CompressViewModel by viewModels()
    private val rulesViewModel: RulesViewModel by viewModels()
    private val historyViewModel: HistoryViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appTheme by settingsViewModel.theme.collectAsStateWithLifecycle()
            ImageCompressorProTheme(appTheme = appTheme) {
                val currentTab by mainViewModel.currentTab.collectAsStateWithLifecycle()
                val showAbout by mainViewModel.showAboutDialog.collectAsStateWithLifecycle()
                val showHelp by mainViewModel.showHelpTutorial.collectAsStateWithLifecycle()
                
                                var showMenu by remember { mutableStateOf(false) }

                val topBarTitle = when (currentTab) {
                    BottomTab.COMPRESS -> "Compress Documents"
                    BottomTab.RULES -> "Document Rules"
                    BottomTab.HISTORY -> "Compression History"
                    BottomTab.SETTINGS -> "Settings"
                }

                Scaffold(
                    topBar = {
                        androidx.compose.material3.TopAppBar(
                            title = { Text(topBarTitle, fontWeight = FontWeight.Bold) },
                            actions = {
                                androidx.compose.material3.IconButton(onClick = { showMenu = true }) {
                                    androidx.compose.material3.Icon(Icons.Rounded.MoreVert, contentDescription = "More options")
                                }
                                androidx.compose.material3.DropdownMenu(
                                    expanded = showMenu,
                                    onDismissRequest = { showMenu = false }
                                ) {
                                    androidx.compose.material3.DropdownMenuItem(
                                        text = { Text("About") },
                                        onClick = {
                                            showMenu = false
                                            mainViewModel.showAboutDialog.value = true
                                        }
                                    )
                                    androidx.compose.material3.DropdownMenuItem(
                                        text = { Text("Help & Tutorial") },
                                        onClick = {
                                            showMenu = false
                                            mainViewModel.showHelpTutorial.value = true
                                        }
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                titleContentColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    },
                    bottomBar = {
                        BottomNavBar(
                            selectedTab = currentTab,
                            onTabSelected = { mainViewModel.selectTab(it) }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .consumeWindowInsets(innerPadding)
                    ) {
                        AnimatedContent(
                            targetState = currentTab,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(150)) togetherWith fadeOut(animationSpec = tween(150))
                            },
                            label = "Tab Transition"
                        ) { tab ->
                            when (tab) {
                                BottomTab.COMPRESS -> CompressScreen(
                                    viewModel = compressViewModel
                                )
                                BottomTab.RULES -> RulesScreen(
                                    viewModel = rulesViewModel
                                )
                                BottomTab.HISTORY -> HistoryScreen(
                                    viewModel = historyViewModel
                                )
                                BottomTab.SETTINGS -> SettingsScreen(
                                    viewModel = settingsViewModel,
                                    rulesViewModel = rulesViewModel
                                )
                            }
                        }
                    }
                }
                
                                // Global Dialogs
                if (showMenu) {
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.padding(top = 64.dp, start = 16.dp)
                    ) {
                        DropdownMenuItem(
                            text = { Text("About") },
                            onClick = {
                                showMenu = false
                                mainViewModel.showAboutDialog.value = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Help & Tutorial") },
                            onClick = {
                                showMenu = false
                                mainViewModel.showHelpTutorial.value = true
                            }
                        )
                    }
                }

                if (showAbout) {
                    AboutDialog(onDismiss = { mainViewModel.showAboutDialog.value = false })
                }
                if (showHelp) {
                    HelpTutorialDialog(onDismiss = { mainViewModel.showHelpTutorial.value = false })
                }
            }
        }
    }
}
