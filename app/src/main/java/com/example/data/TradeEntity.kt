package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trades")
data class TradeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val asset: String,
    val direction: String, // "Long" or "Short"
    val entryPrice: Double,
    val stopLoss: Double,
    val takeProfit: Double,
    val exitPrice: Double,
    val executionType: String = "Manual", // "Manual" or "Algorithmic"
    val lotSize: Double,
    val profitOrLoss: Double, // USD
    val rMultiple: Double, // Realized R:R
    val strategy: String, // e.g., "Fair Value Gap (FVG)", "Order Block (OB)", "Liquidity Sweep"
    val timeframe: String, // e.g., "M15", "H1", "M5"
    val poiNotes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
