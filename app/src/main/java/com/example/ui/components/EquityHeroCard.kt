package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.model.TradeAnalytics
import com.example.ui.theme.AccentHeroText
import com.example.ui.theme.AccentPrimary
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.LossRed
import com.example.ui.theme.LossRedBorder
import com.example.ui.theme.LossRedContainer
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.ProfitGreenBorder
import com.example.ui.theme.ProfitGreenContainer
import com.example.ui.theme.TextMuted
import java.util.Locale

@Composable
fun EquityHeroCard(
    analytics: TradeAnalytics,
    modifier: Modifier = Modifier
) {
    val isProfit = analytics.totalPnL >= 0
    val pnlSign = if (isProfit) "+" else "-"
    val absPnL = kotlin.math.abs(analytics.totalPnL)
    val formattedPnL = String.format(Locale.US, "$pnlSign$%,.2f", absPnL)

    val returnSign = if (analytics.returnPercentage >= 0) "+" else ""
    val formattedReturn = String.format(Locale.US, "$returnSign%.1f%%", analytics.returnPercentage)

    val winRateFormatted = String.format(Locale.US, "%.1f%%", analytics.winRate)
    val avgRRFormatted = if (analytics.avgRiskReward >= 0) {
        String.format(Locale.US, "1:%.1f", kotlin.math.max(analytics.avgRiskReward, 1.0))
    } else {
        String.format(Locale.US, "%.1fR", analytics.avgRiskReward)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(DarkSurface)
            .border(1.dp, DarkBorder, RoundedCornerShape(24.dp))
            .padding(18.dp)
            .testTag("equity_hero_card")
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top row: Label & Return Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "TOTAL EQUITY PNL",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = formattedPnL,
                        color = if (isProfit) AccentHeroText else LossRed,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // Percentage Return Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isProfit) ProfitGreenContainer else LossRedContainer)
                        .border(
                            1.dp,
                            if (isProfit) ProfitGreenBorder else LossRedBorder,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = formattedReturn,
                        color = if (isProfit) ProfitGreen else LossRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 3-Column Metrics Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Metric 1: Win Rate
                MetricBox(
                    label = "WIN RATE",
                    value = winRateFormatted,
                    valueColor = Color.White,
                    modifier = Modifier.weight(1f)
                )

                // Metric 2: Avg R:R
                MetricBox(
                    label = "AVG R:R",
                    value = avgRRFormatted,
                    valueColor = Color.White,
                    modifier = Modifier.weight(1f)
                )

                // Metric 3: Best Asset
                MetricBox(
                    label = "BEST ASSET",
                    value = analytics.bestAsset,
                    valueColor = AccentPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MetricBox(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurfaceVariant)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = label,
                color = TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            )
            Text(
                text = value,
                color = valueColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}
