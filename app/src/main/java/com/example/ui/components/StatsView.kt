package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TradeEntity
import com.example.ui.model.TradeAnalytics
import com.example.ui.theme.AccentHeroText
import com.example.ui.theme.AccentPrimary
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSubtleBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.LossRed
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.TextMain
import com.example.ui.theme.TextMuted
import java.util.Locale

@Composable
fun StatsView(
    analytics: TradeAnalytics,
    trades: List<TradeEntity>,
    modifier: Modifier = Modifier
) {
    val strategyStats = trades.groupBy { it.strategy }
        .mapValues { entry ->
            val list = entry.value
            val wins = list.count { it.profitOrLoss > 0 }
            val totalPnL = list.sumOf { it.profitOrLoss }
            val winPct = if (list.isNotEmpty()) (wins.toDouble() / list.size.toDouble()) * 100.0 else 0.0
            Triple(list.size, winPct, totalPnL)
        }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("stats_view"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Header
        Text(
            text = "// PERFORMANCE ANALYTICS",
            color = TextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
        )

        // Summary Card Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = "PROFIT FACTOR",
                value = String.format(Locale.US, "%.2f", analytics.profitFactor),
                color = if (analytics.profitFactor >= 1.5) ProfitGreen else AccentHeroText,
                modifier = Modifier.weight(1f)
            )

            StatCard(
                title = "TOTAL EXECUTIONS",
                value = "${analytics.totalTradesCount}",
                color = TextMain,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = "BEST TRADE",
                value = String.format(Locale.US, "+$%,.2f", analytics.bestTradePnL),
                color = ProfitGreen,
                modifier = Modifier.weight(1f)
            )

            StatCard(
                title = "MAX DRAWDOWN",
                value = String.format(Locale.US, "$%,.2f", analytics.worstTradePnL),
                color = LossRed,
                modifier = Modifier.weight(1f)
            )
        }

        // Win/Loss Ratio Breakdown Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurface)
                .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "WIN / LOSS DISTRIBUTION",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${analytics.winningTradesCount} Wins (${String.format(Locale.US, "%.1f", analytics.winRate)}%)",
                        color = ProfitGreen,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${analytics.losingTradesCount} Losses",
                        color = LossRed,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Distribution Bar
                val winFraction = if (analytics.totalTradesCount > 0) {
                    (analytics.winningTradesCount.toFloat() / analytics.totalTradesCount.toFloat()).coerceIn(0.05f, 0.95f)
                } else 0.5f

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .weight(winFraction)
                            .fillMaxSize()
                            .background(ProfitGreen)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f - winFraction)
                            .fillMaxSize()
                            .background(LossRed)
                    )
                }
            }
        }

        // Strategy Breakdown Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurface)
                .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "SMC STRATEGY PERFORMANCE",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )

                if (strategyStats.isEmpty()) {
                    Text(
                        text = "No trades logged yet.",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                } else {
                    strategyStats.forEach { (strategyName, data) ->
                        val (count, winPct, pnl) = data
                        val isProfitable = pnl >= 0
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkSurfaceVariant)
                                .border(1.dp, DarkSubtleBorder, RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = strategyName,
                                    color = AccentPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "$count trades • ${String.format(Locale.US, "%.0f%% win rate", winPct)}",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }

                            Text(
                                text = String.format(Locale.US, "${if (isProfitable) "+" else ""}$%,.2f", pnl),
                                color = if (isProfitable) ProfitGreen else LossRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurface)
            .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = title,
                color = TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            )
            Text(
                text = value,
                color = color,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
