package com.example.ui.model

data class TradeAnalytics(
    val totalPnL: Double = 0.0,
    val returnPercentage: Double = 0.0,
    val winRate: Double = 0.0,
    val avgRiskReward: Double = 0.0,
    val bestAsset: String = "N/A",
    val totalTradesCount: Int = 0,
    val winningTradesCount: Int = 0,
    val losingTradesCount: Int = 0,
    val profitFactor: Double = 0.0,
    val bestTradePnL: Double = 0.0,
    val worstTradePnL: Double = 0.0
)

enum class TradeFilter(val displayName: String) {
    ALL("All"),
    PROFITS("Wins"),
    LOSSES("Losses"),
    FVG("FVG"),
    ORDER_BLOCK("OB"),
    LIQUIDITY_SWEEP("Liq Sweep"),
    BREAKER("Breaker"),
    BOS("BOS")
}

enum class NavigationTab(val label: String) {
    HOME("Home"),
    JOURNAL("Journal"),
    STATS("Stats"),
    STRATEGIES("SMC Playbook")
}
