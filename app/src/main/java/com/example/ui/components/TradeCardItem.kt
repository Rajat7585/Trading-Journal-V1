package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TradeEntity
import com.example.ui.theme.AccentPrimary
import com.example.ui.theme.ChipBackground
import com.example.ui.theme.ChipBorder
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
fun TradeCardItem(
    trade: TradeEntity,
    onEdit: (TradeEntity) -> Unit,
    onDelete: (TradeEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val isProfit = trade.profitOrLoss >= 0
    val pnlSign = if (isProfit) "+" else "-"
    val absPnL = kotlin.math.abs(trade.profitOrLoss)
    val formattedPnL = String.format(Locale.US, "$pnlSign$%,.2f", absPnL)
    val rFormatted = String.format(Locale.US, "%.1fR", trade.rMultiple)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurface)
            .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
            .clickable { expanded = !expanded }
            .padding(14.dp)
            .testTag("trade_card_${trade.id}")
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left side: Indicator Bar + Details
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Vertical Status Bar
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(36.dp)
                            .clip(CircleShape)
                            .background(if (isProfit) ProfitGreen else LossRed)
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = trade.asset,
                                color = TextMain,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )

                            // Strategy Chip
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(ChipBackground)
                                    .border(1.dp, ChipBorder, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = trade.strategy,
                                    color = AccentPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Text(
                            text = "${trade.timeframe} ${trade.direction} • ${trade.lotSize} Lots",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                // Right side: PnL & R-Multiple
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    Text(
                        text = formattedPnL,
                        color = if (isProfit) ProfitGreen else LossRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = rFormatted,
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Expanded Details
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(DarkBorder)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Detail Metrics Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DetailItem("Entry", String.format(Locale.US, "%.5f", trade.entryPrice).trimEnd('0').trimEnd('.'))
                        DetailItem("Exit", String.format(Locale.US, "%.5f", trade.exitPrice).trimEnd('0').trimEnd('.'))
                        DetailItem("Stop Loss", String.format(Locale.US, "%.5f", trade.stopLoss).trimEnd('0').trimEnd('.'))
                        DetailItem("Take Profit", String.format(Locale.US, "%.5f", trade.takeProfit).trimEnd('0').trimEnd('.'))
                    }

                    if (trade.poiNotes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkSurfaceVariant)
                                .border(1.dp, DarkSubtleBorder, RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "POI: ${trade.poiNotes}",
                                color = TextMuted,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(DarkSurfaceVariant)
                                .border(1.dp, DarkSubtleBorder, RoundedCornerShape(6.dp))
                                .clickable { onEdit(trade) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("edit_trade_${trade.id}"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = AccentPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(text = "Edit", color = AccentPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(DarkSurfaceVariant)
                                .border(1.dp, LossRed.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                .clickable { onDelete(trade) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("delete_trade_${trade.id}"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = LossRed,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(text = "Delete", color = LossRed, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailItem(label: String, value: String) {
    Column {
        Text(text = label, color = TextMuted, fontSize = 10.sp)
        Text(text = value, color = TextMain, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}
