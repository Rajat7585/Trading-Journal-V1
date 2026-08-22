package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.TradeEntity
import com.example.ui.theme.AccentDeepNavy
import com.example.ui.theme.AccentPrimary
import com.example.ui.theme.ChipBackground
import com.example.ui.theme.ChipBorder
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSubtleBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.LossRed
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.TextMain
import com.example.ui.theme.TextMuted
import java.util.Locale
import kotlin.math.abs

@Composable
fun AddTradeDialog(
    tradeToEdit: TradeEntity?,
    onDismiss: () -> Unit,
    onSave: (
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
    ) -> Unit
) {
    var asset by remember { mutableStateOf(tradeToEdit?.asset ?: "XAUUSD") }
    var direction by remember { mutableStateOf(tradeToEdit?.direction ?: "Long") }
    var entryPriceStr by remember { mutableStateOf(tradeToEdit?.entryPrice?.toString() ?: "2415.00") }
    var stopLossStr by remember { mutableStateOf(tradeToEdit?.stopLoss?.toString() ?: "2410.00") }
    var takeProfitStr by remember { mutableStateOf(tradeToEdit?.takeProfit?.toString() ?: "2427.00") }
    var exitPriceStr by remember { mutableStateOf(tradeToEdit?.exitPrice?.toString() ?: "2427.00") }
    var lotSizeStr by remember { mutableStateOf(tradeToEdit?.lotSize?.toString() ?: "0.5") }
    var executionType by remember { mutableStateOf(tradeToEdit?.executionType ?: "Manual") }
    var pnlStr by remember { mutableStateOf(tradeToEdit?.profitOrLoss?.toString() ?: "600.00") }
    var strategy by remember { mutableStateOf(tradeToEdit?.strategy ?: "FVG") }
    var timeframe by remember { mutableStateOf(tradeToEdit?.timeframe ?: "M15") }
    var poiNotes by remember { mutableStateOf(tradeToEdit?.poiNotes ?: "") }

    val calculatedRMultiple by remember {
        derivedStateOf {
            val entry = entryPriceStr.toDoubleOrNull() ?: 0.0
            val sl = stopLossStr.toDoubleOrNull() ?: 0.0
            val exit = exitPriceStr.toDoubleOrNull() ?: 0.0
            val risk = abs(entry - sl)
            if (risk > 0.00001) {
                val reward = if (direction.equals("Long", ignoreCase = true)) exit - entry else entry - exit
                reward / risk
            } else {
                0.0
            }
        }
    }

    val strategiesList = listOf("FVG", "OB", "Liq Sweep", "Breaker", "BOS", "CHoCH")
    val timeframesList = listOf("M1", "M5", "M15", "H1", "H4", "D")
    val assetPresets = listOf("XAUUSD", "EURUSD", "BTCUSD", "GBPUSD", "US30", "ETHUSD")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, DarkBorder, RoundedCornerShape(20.dp)),
            color = DarkSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Dialog Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (tradeToEdit != null) "// EDIT EXECUTION" else "// LOG NEW TRADE",
                            color = AccentPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Record Smart Money confluence & metrics",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Asset Input & Preset Chips
                Text("Asset / Pair", color = TextMuted, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))
                TradeTextField(
                    value = asset,
                    onValueChange = { asset = it.uppercase() },
                    placeholder = "e.g. XAUUSD",
                    modifier = Modifier.testTag("input_asset")
                )

                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    assetPresets.forEach { preset ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (asset == preset) AccentPrimary else DarkSurfaceVariant)
                                .border(1.dp, DarkSubtleBorder, RoundedCornerShape(6.dp))
                                .clickable { asset = preset }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = preset,
                                color = if (asset == preset) AccentDeepNavy else TextMain,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Direction Toggle (Long / Short)
                Text("Direction", color = TextMuted, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (direction == "Long") ProfitGreen else DarkSurfaceVariant)
                            .border(1.dp, if (direction == "Long") ProfitGreen else DarkBorder, RoundedCornerShape(8.dp))
                            .clickable { direction = "Long" }
                            .padding(vertical = 10.dp)
                            .testTag("direction_long"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "▲ LONG (BUY)",
                            color = if (direction == "Long") AccentDeepNavy else TextMain,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (direction == "Short") LossRed else DarkSurfaceVariant)
                            .border(1.dp, if (direction == "Short") LossRed else DarkBorder, RoundedCornerShape(8.dp))
                            .clickable { direction = "Short" }
                            .padding(vertical = 10.dp)
                            .testTag("direction_short"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "▼ SHORT (SELL)",
                            color = if (direction == "Short") AccentDeepNavy else TextMain,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Entry & Exit Price
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Entry Price", color = TextMuted, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        TradeTextField(
                            value = entryPriceStr,
                            onValueChange = { entryPriceStr = it },
                            keyboardType = KeyboardType.Decimal,
                            modifier = Modifier.testTag("input_entry_price")
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Exit Price", color = TextMuted, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        TradeTextField(
                            value = exitPriceStr,
                            onValueChange = { exitPriceStr = it },
                            keyboardType = KeyboardType.Decimal,
                            modifier = Modifier.testTag("input_exit_price")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Stop Loss & Take Profit
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Stop Loss", color = TextMuted, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        TradeTextField(
                            value = stopLossStr,
                            onValueChange = { stopLossStr = it },
                            keyboardType = KeyboardType.Decimal,
                            modifier = Modifier.testTag("input_stop_loss")
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Take Profit", color = TextMuted, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        TradeTextField(
                            value = takeProfitStr,
                            onValueChange = { takeProfitStr = it },
                            keyboardType = KeyboardType.Decimal,
                            modifier = Modifier.testTag("input_take_profit")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Lot Size, Execution Type & PnL
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Lot Size", color = TextMuted, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        TradeTextField(
                            value = lotSizeStr,
                            onValueChange = { lotSizeStr = it },
                            keyboardType = KeyboardType.Decimal,
                            modifier = Modifier.testTag("input_lot_size")
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("PnL ($ USD)", color = TextMuted, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        TradeTextField(
                            value = pnlStr,
                            onValueChange = { pnlStr = it },
                            keyboardType = KeyboardType.Number,
                            placeholder = "e.g. 420.00",
                            modifier = Modifier.testTag("input_pnl")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Auto R-Multiple Indicator
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkSurfaceVariant)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Calculated Expectancy:", color = TextMuted, fontSize = 11.sp)
                        Text(
                            text = String.format(Locale.US, "%.2f R", calculatedRMultiple),
                            color = if (calculatedRMultiple >= 0) ProfitGreen else LossRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Strategy selector
                Text("SMC Strategy Setup", color = TextMuted, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    strategiesList.forEach { item ->
                        val isSelected = strategy == item
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) AccentPrimary else ChipBackground)
                                .border(1.dp, if (isSelected) AccentPrimary else ChipBorder, RoundedCornerShape(6.dp))
                                .clickable { strategy = item }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = item,
                                color = if (isSelected) AccentDeepNavy else TextMain,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Timeframe selector
                Text("Execution Timeframe", color = TextMuted, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    timeframesList.forEach { tf ->
                        val isSelected = timeframe == tf
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) AccentPrimary else ChipBackground)
                                .border(1.dp, if (isSelected) AccentPrimary else ChipBorder, RoundedCornerShape(6.dp))
                                .clickable { timeframe = tf }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = tf,
                                color = if (isSelected) AccentDeepNavy else TextMain,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Execution Type toggle (Manual / EA)
                Text("Execution Type", color = TextMuted, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Manual", "Algorithmic").forEach { type ->
                        val isSelected = executionType == type
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) AccentPrimary else DarkSurfaceVariant)
                                .border(1.dp, if (isSelected) AccentPrimary else DarkBorder, RoundedCornerShape(6.dp))
                                .clickable { executionType = type }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = type,
                                color = if (isSelected) AccentDeepNavy else TextMain,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // POI Notes
                Text("POI & Confluence Notes", color = TextMuted, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))
                TradeTextField(
                    value = poiNotes,
                    onValueChange = { poiNotes = it },
                    placeholder = "e.g. Asian session low sweep into M15 Bullish FVG",
                    singleLine = false,
                    modifier = Modifier.testTag("input_poi_notes")
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Submit Button
                Button(
                    onClick = {
                        val entry = entryPriceStr.toDoubleOrNull() ?: 0.0
                        val sl = stopLossStr.toDoubleOrNull() ?: 0.0
                        val tp = takeProfitStr.toDoubleOrNull() ?: 0.0
                        val exit = exitPriceStr.toDoubleOrNull() ?: 0.0
                        val lots = lotSizeStr.toDoubleOrNull() ?: 1.0
                        val pnl = pnlStr.toDoubleOrNull() ?: 0.0

                        onSave(
                            asset.ifBlank { "XAUUSD" },
                            direction,
                            entry,
                            sl,
                            tp,
                            exit,
                            executionType,
                            lots,
                            pnl,
                            strategy,
                            timeframe,
                            poiNotes
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_trade_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentPrimary,
                        contentColor = AccentDeepNavy
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (tradeToEdit != null) "UPDATE EXECUTION" else "COMMIT TO JOURNAL",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TradeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = TextMuted, fontSize = 12.sp) },
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextMain,
            unfocusedTextColor = TextMain,
            focusedContainerColor = DarkSurfaceVariant,
            unfocusedContainerColor = DarkSurfaceVariant,
            focusedBorderColor = AccentPrimary,
            unfocusedBorderColor = DarkBorder,
            cursorColor = AccentPrimary
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.fillMaxWidth()
    )
}
