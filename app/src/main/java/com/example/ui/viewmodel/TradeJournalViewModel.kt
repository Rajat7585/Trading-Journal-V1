package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.TradeEntity
import com.example.data.TradeRepository
import com.example.ui.model.NavigationTab
import com.example.ui.model.TradeAnalytics
import com.example.ui.model.TradeFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.abs

class TradeJournalViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TradeRepository

    private val _selectedFilter = MutableStateFlow(TradeFilter.ALL)
    val selectedFilter: StateFlow<TradeFilter> = _selectedFilter

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedTab = MutableStateFlow(NavigationTab.HOME)
    val selectedTab: StateFlow<NavigationTab> = _selectedTab

    private val _editingTrade = MutableStateFlow<TradeEntity?>(null)
    val editingTrade: StateFlow<TradeEntity?> = _editingTrade

    private val _isAddDialogOpen = MutableStateFlow(false)
    val isAddDialogOpen: StateFlow<Boolean> = _isAddDialogOpen

    val allTrades: StateFlow<List<TradeEntity>>

    val filteredTrades: StateFlow<List<TradeEntity>>

    val analytics: StateFlow<TradeAnalytics>

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = TradeRepository(database.tradeDao())

        allTrades = repository.allTrades
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        filteredTrades = combine(allTrades, _selectedFilter, _searchQuery) { trades, filter, query ->
            trades.filter { trade ->
                val matchesQuery = query.isBlank() ||
                        trade.asset.contains(query, ignoreCase = true) ||
                        trade.strategy.contains(query, ignoreCase = true) ||
                        trade.timeframe.contains(query, ignoreCase = true) ||
                        trade.poiNotes.contains(query, ignoreCase = true)

                val matchesFilter = when (filter) {
                    TradeFilter.ALL -> true
                    TradeFilter.PROFITS -> trade.profitOrLoss > 0
                    TradeFilter.LOSSES -> trade.profitOrLoss <= 0
                    TradeFilter.FVG -> trade.strategy.contains("FVG", ignoreCase = true)
                    TradeFilter.ORDER_BLOCK -> trade.strategy.contains("OB", ignoreCase = true) || trade.strategy.contains("Order Block", ignoreCase = true)
                    TradeFilter.LIQUIDITY_SWEEP -> trade.strategy.contains("Sweep", ignoreCase = true) || trade.strategy.contains("Liq", ignoreCase = true)
                    TradeFilter.BREAKER -> trade.strategy.contains("Breaker", ignoreCase = true)
                    TradeFilter.BOS -> trade.strategy.contains("BOS", ignoreCase = true) || trade.strategy.contains("Structure", ignoreCase = true)
                }

                matchesQuery && matchesFilter
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        analytics = allTrades.combine(_selectedFilter) { trades, _ ->
            calculateAnalytics(trades)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TradeAnalytics()
        )
    }

    private fun calculateAnalytics(trades: List<TradeEntity>): TradeAnalytics {
        if (trades.isEmpty()) {
            return TradeAnalytics()
        }

        val totalPnL = trades.sumOf { it.profitOrLoss }
        val winningTrades = trades.filter { it.profitOrLoss > 0 }
        val losingTrades = trades.filter { it.profitOrLoss < 0 }

        val totalCount = trades.size
        val winCount = winningTrades.size
        val lossCount = losingTrades.size

        val winRate = if (totalCount > 0) (winCount.toDouble() / totalCount.toDouble()) * 100.0 else 0.0

        val avgRR = if (trades.isNotEmpty()) {
            val totalR = trades.sumOf { it.rMultiple }
            totalR / trades.size
        } else 0.0

        val assetPnLMap = trades.groupBy { it.asset.uppercase() }
            .mapValues { entry -> entry.value.sumOf { it.profitOrLoss } }

        val bestAsset = assetPnLMap.maxByOrNull { it.value }?.key ?: "N/A"

        val grossProfit = winningTrades.sumOf { it.profitOrLoss }
        val grossLoss = abs(losingTrades.sumOf { it.profitOrLoss })
        val profitFactor = if (grossLoss > 0) grossProfit / grossLoss else if (grossProfit > 0) grossProfit else 1.0

        // Estimated return percentage relative to $300k base equity
        val returnPercentage = (totalPnL / 300000.0) * 100.0

        val bestTrade = trades.maxOfOrNull { it.profitOrLoss } ?: 0.0
        val worstTrade = trades.minOfOrNull { it.profitOrLoss } ?: 0.0

        return TradeAnalytics(
            totalPnL = totalPnL,
            returnPercentage = returnPercentage,
            winRate = winRate,
            avgRiskReward = avgRR,
            bestAsset = bestAsset,
            totalTradesCount = totalCount,
            winningTradesCount = winCount,
            losingTradesCount = lossCount,
            profitFactor = profitFactor,
            bestTradePnL = bestTrade,
            worstTradePnL = worstTrade
        )
    }

    fun setFilter(filter: TradeFilter) {
        _selectedFilter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedTab(tab: NavigationTab) {
        _selectedTab.value = tab
    }

    fun openAddDialog(tradeToEdit: TradeEntity? = null) {
        _editingTrade.value = tradeToEdit
        _isAddDialogOpen.value = true
    }

    fun closeAddDialog() {
        _editingTrade.value = null
        _isAddDialogOpen.value = false
    }

    fun saveTrade(
        asset: String,
        direction: String,
        entryPrice: Double,
        stopLoss: Double,
        takeProfit: Double,
        exitPrice: Double,
        executionType: String,
        lotSize: Double,
        profitOrLoss: Double,
        strategy: String,
        timeframe: String,
        poiNotes: String
    ) {
        viewModelScope.launch {
            // Auto compute R multiple if risk is present
            val riskPerUnit = abs(entryPrice - stopLoss)
            val rMultiple = if (riskPerUnit > 0.000001) {
                val reward = if (direction.equals("Long", ignoreCase = true)) {
                    exitPrice - entryPrice
                } else {
                    entryPrice - exitPrice
                }
                (reward / riskPerUnit)
            } else {
                if (profitOrLoss > 0) 2.0 else -1.0
            }

            val roundedR = Math.round(rMultiple * 10.0) / 10.0

            val currentEdit = _editingTrade.value
            if (currentEdit != null) {
                val updated = currentEdit.copy(
                    asset = asset.trim().uppercase(),
                    direction = direction,
                    entryPrice = entryPrice,
                    stopLoss = stopLoss,
                    takeProfit = takeProfit,
                    exitPrice = exitPrice,
                    executionType = executionType,
                    lotSize = lotSize,
                    profitOrLoss = profitOrLoss,
                    rMultiple = roundedR,
                    strategy = strategy,
                    timeframe = timeframe,
                    poiNotes = poiNotes
                )
                repository.update(updated)
            } else {
                val newTrade = TradeEntity(
                    asset = asset.trim().uppercase(),
                    direction = direction,
                    entryPrice = entryPrice,
                    stopLoss = stopLoss,
                    takeProfit = takeProfit,
                    exitPrice = exitPrice,
                    executionType = executionType,
                    lotSize = lotSize,
                    profitOrLoss = profitOrLoss,
                    rMultiple = roundedR,
                    strategy = strategy,
                    timeframe = timeframe,
                    poiNotes = poiNotes
                )
                repository.insert(newTrade)
            }
            closeAddDialog()
        }
    }

    fun deleteTrade(trade: TradeEntity) {
        viewModelScope.launch {
            repository.delete(trade)
        }
    }

    fun resetToSampleData() {
        viewModelScope.launch {
            repository.resetWithSampleData()
        }
    }
}
