package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.ui.theme.AccentDeepNavy
import com.example.ui.theme.AccentPrimary
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSubtleBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.TextMain
import com.example.ui.theme.TextMuted

data class SmcStrategyGuide(
    val title: String,
    val tag: String,
    val description: String,
    val rules: List<String>
)

@Composable
fun PlaybookView(
    onLogWithStrategy: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val playbookItems = listOf(
        SmcStrategyGuide(
            title = "Fair Value Gap (FVG)",
            tag = "FVG",
            description = "Three-candle price imbalance leaving an inefficient pocket that smart money re-enters to balance liquidity.",
            rules = listOf(
                "Candle 1 high and Candle 3 low do not overlap",
                "Energetic displacement candle in between",
                "Enter on 50% equilibrium mitigation or touch"
            )
        ),
        SmcStrategyGuide(
            title = "Order Block (OB)",
            tag = "OB",
            description = "The last opposing institutional candle before strong momentum shift that breaks market structure.",
            rules = listOf(
                "Must lead to a clean Break of Structure (BOS)",
                "Accompanied by volume / displacement",
                "Limit entry at candle open or 50% wick"
            )
        ),
        SmcStrategyGuide(
            title = "Liquidity Sweep",
            tag = "Liq Sweep",
            description = "False breakout above key highs or below key lows engineered to trigger stop losses and capture resting orders.",
            rules = listOf(
                "Asian High/Low, Previous Day High/Low sweeps",
                "Wick rejection with closed body inside range",
                "Immediate MS Shift on lower timeframe (M1/M5)"
            )
        ),
        SmcStrategyGuide(
            title = "Breaker Block",
            tag = "Breaker",
            description = "A failed order block where price swept liquidity, broke past the previous OB, and now acts as inverse support/resistance.",
            rules = listOf(
                "Higher high formed that took liquidity",
                "Aggressive impulsive breakdown through original demand",
                "Demand flips to supply on retest"
            )
        ),
        SmcStrategyGuide(
            title = "Break of Structure (BOS)",
            tag = "BOS",
            description = "Clean continuation pattern when price closes with candle body beyond the previous structural swing point.",
            rules = listOf(
                "Body close above swing high (Bullish) or below low (Bearish)",
                "Confirms higher-timeframe trend continuation",
                "Look for internal retracement into discount POI"
            )
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("playbook_view"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "// SMART MONEY CONCEPTS PLAYBOOK",
            color = TextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
        )

        playbookItems.forEach { item ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkSurface)
                    .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.title,
                            color = TextMain,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(DarkSurfaceVariant)
                                .border(1.dp, DarkSubtleBorder, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = item.tag,
                                color = AccentPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = item.description,
                        color = TextMuted,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkSurfaceVariant)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "EXECUTION CRITERIA:",
                            color = AccentPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        item.rules.forEach { rule ->
                            Text(
                                text = "• $rule",
                                color = TextMain,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AccentPrimary)
                                .clickable { onLogWithStrategy(item.tag) }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Log + ${item.tag}",
                                color = AccentDeepNavy,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
