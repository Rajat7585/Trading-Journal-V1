package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AddTradeDialog
import com.example.ui.components.BottomNavBar
import com.example.ui.components.EquityHeroCard
import com.example.ui.components.FilterBar
import com.example.ui.components.HeaderBar
import com.example.ui.components.PlaybookView
import com.example.ui.components.StatsView
import com.example.ui.components.TradeCardItem
import com.example.ui.model.NavigationTab
import com.example.ui.model.TradeFilter
import com.example.ui.theme.AccentPrimary
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TradeArchiveTheme
import com.example.ui.viewmodel.TradeJournalViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: TradeJournalViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TradeArchiveTheme {
                TradeArchiveApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun TradeArchiveApp(
    viewModel: TradeJournalViewModel,
    modifier: Modifier = Modifier
) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val analytics by viewModel.analytics.collectAsStateWithLifecycle()
    val filteredTrades by viewModel.filteredTrades.collectAsStateWithLifecycle()
    val allTrades by viewModel.allTrades.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isAddDialogOpen by viewModel.isAddDialogOpen.collectAsStateWithLifecycle()
    val editingTrade by viewModel.editingTrade.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground),
        containerColor = DarkBackground,
        bottomBar = {
            BottomNavBar(
                selectedTab = selectedTab,
                onSelectTab = { viewModel.setSelectedTab(it) },
                onAddTradeClick = { viewModel.openAddDialog(null) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBackground),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 600.dp)
            ) {
                // Top Header Bar
                HeaderBar(
                    onResetData = { viewModel.resetToSampleData() },
                    modifier = Modifier.statusBarsPadding()
                )

                // Tab Content Switcher
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "tab_transition",
                    modifier = Modifier.weight(1f)
                ) { currentTab ->
                    when (currentTab) {
                        NavigationTab.HOME -> {
                            HomeTabContent(
                                analytics = analytics,
                                trades = filteredTrades,
                                selectedFilter = selectedFilter,
                                onSelectFilter = { viewModel.setFilter(it) },
                                searchQuery = searchQuery,
                                onSearchChange = { viewModel.setSearchQuery(it) },
                                onEditTrade = { viewModel.openAddDialog(it) },
                                onDeleteTrade = { viewModel.deleteTrade(it) },
                                onViewAllClick = { viewModel.setSelectedTab(NavigationTab.JOURNAL) }
                            )
                        }

                        NavigationTab.JOURNAL -> {
                            JournalTabContent(
                                trades = filteredTrades,
                                selectedFilter = selectedFilter,
                                onSelectFilter = { viewModel.setFilter(it) },
                                searchQuery = searchQuery,
                                onSearchChange = { viewModel.setSearchQuery(it) },
                                onEditTrade = { viewModel.openAddDialog(it) },
                                onDeleteTrade = { viewModel.deleteTrade(it) }
                            )
                        }

                        NavigationTab.STATS -> {
                            StatsView(
                                analytics = analytics,
                                trades = allTrades
                            )
                        }

                        NavigationTab.STRATEGIES -> {
                            PlaybookView(
                                onLogWithStrategy = { strategyTag ->
                                    viewModel.openAddDialog(null)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Add / Edit Trade Dialog
    if (isAddDialogOpen) {
        AddTradeDialog(
            tradeToEdit = editingTrade,
            onDismiss = { viewModel.closeAddDialog() },
            onSave = { asset, direction, entry, sl, tp, exit, execType, lots, pnl, strategy, tf, poi ->
                viewModel.saveTrade(
                    asset = asset,
                    direction = direction,
                    entryPrice = entry,
                    stopLoss = sl,
                    takeProfit = tp,
                    exitPrice = exit,
                    executionType = execType,
                    lotSize = lots,
                    profitOrLoss = pnl,
                    strategy = strategy,
                    timeframe = tf,
                    poiNotes = poi
                )
            }
        )
    }
}

@Composable
private fun HomeTabContent(
    analytics: com.example.ui.model.TradeAnalytics,
    trades: List<com.example.data.TradeEntity>,
    selectedFilter: TradeFilter,
    onSelectFilter: (TradeFilter) -> Unit,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onEditTrade: (com.example.data.TradeEntity) -> Unit,
    onDeleteTrade: (com.example.data.TradeEntity) -> Unit,
    onViewAllClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("home_feed_list"),
        contentPadding = PaddingValues(vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Hero Portfolio Card
        item {
            EquityHeroCard(analytics = analytics)
        }

        // Section Title: Recent Journal & View All
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECENT JOURNAL",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )

                TextButton(
                    onClick = onViewAllClick,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "View All",
                        color = AccentPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Filter Bar
        item {
            FilterBar(
                selectedFilter = selectedFilter,
                onSelectFilter = onSelectFilter,
                searchQuery = searchQuery,
                onSearchChange = onSearchChange
            )
        }

        // Trades List
        if (trades.isEmpty()) {
            item {
                EmptyStateCard()
            }
        } else {
            items(trades, key = { it.id }) { trade ->
                TradeCardItem(
                    trade = trade,
                    onEdit = onEditTrade,
                    onDelete = onDeleteTrade
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun JournalTabContent(
    trades: List<com.example.data.TradeEntity>,
    selectedFilter: TradeFilter,
    onSelectFilter: (TradeFilter) -> Unit,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onEditTrade: (com.example.data.TradeEntity) -> Unit,
    onDeleteTrade: (com.example.data.TradeEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("journal_feed_list"),
        contentPadding = PaddingValues(vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "// FULL EXECUTION LEDGER",
                color = TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
        }

        item {
            FilterBar(
                selectedFilter = selectedFilter,
                onSelectFilter = onSelectFilter,
                searchQuery = searchQuery,
                onSearchChange = onSearchChange
            )
        }

        if (trades.isEmpty()) {
            item {
                EmptyStateCard()
            }
        } else {
            items(trades, key = { it.id }) { trade ->
                TradeCardItem(
                    trade = trade,
                    onEdit = onEditTrade,
                    onDelete = onDeleteTrade
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun EmptyStateCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "No Executions Found",
                color = TextMuted,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Tap the + button to log your first trade setup.",
                color = TextMuted.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }
    }
}
