package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import com.example.ui.theme.AccentDeepNavy
import com.example.ui.theme.AccentPrimary
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSubtleBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.TextMain
import com.example.ui.theme.TextMuted

@Composable
fun HeaderBar(
    onResetData: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkSurface)
            .border(width = 1.dp, color = DarkBorder, shape = RoundedCornerShape(0.dp))
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo + Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(AccentPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "TA",
                    color = AccentDeepNavy,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Column {
                Text(
                    text = "TradeArchive",
                    color = TextMain,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp,
                    letterSpacing = (-0.3).sp
                )
            }
        }

        // User Avatar badge / menu
        Box {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(DarkSurfaceVariant)
                    .border(1.dp, DarkSubtleBorder, CircleShape)
                    .clickable { menuExpanded = true }
                    .testTag("user_profile_avatar"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "JD",
                    color = AccentPrimary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
                )
            }

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
                modifier = Modifier.background(DarkSurfaceVariant)
            ) {
                DropdownMenuItem(
                    text = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = AccentPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text("Reset Sample Data", color = TextMain, fontSize = 13.sp)
                        }
                    },
                    onClick = {
                        menuExpanded = false
                        onResetData()
                    }
                )
            }
        }
    }
}
