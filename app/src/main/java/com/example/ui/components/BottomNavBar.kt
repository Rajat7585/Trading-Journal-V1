package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.model.NavigationTab
import com.example.ui.theme.AccentContainer
import com.example.ui.theme.AccentPrimary
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextOnAccent

@Composable
fun BottomNavBar(
    selectedTab: NavigationTab,
    onSelectTab: (NavigationTab) -> Unit,
    onAddTradeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Main Navigation Bar Container
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface)
                .border(1.dp, DarkBorder, RoundedCornerShape(0.dp))
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left 2 items
            NavItem(
                icon = Icons.Default.Home,
                label = "Home",
                isSelected = selectedTab == NavigationTab.HOME,
                onClick = { onSelectTab(NavigationTab.HOME) },
                testTag = "nav_home"
            )

            NavItem(
                icon = Icons.Default.FormatListBulleted,
                label = "Journal",
                isSelected = selectedTab == NavigationTab.JOURNAL,
                onClick = { onSelectTab(NavigationTab.JOURNAL) },
                testTag = "nav_journal"
            )

            // Center Spacer for elevated FAB button
            Box(modifier = Modifier.width(48.dp))

            // Right 2 items
            NavItem(
                icon = Icons.Default.Analytics,
                label = "Stats",
                isSelected = selectedTab == NavigationTab.STATS,
                onClick = { onSelectTab(NavigationTab.STATS) },
                testTag = "nav_stats"
            )

            NavItem(
                icon = Icons.Default.AutoStories,
                label = "Playbook",
                isSelected = selectedTab == NavigationTab.STRATEGIES,
                onClick = { onSelectTab(NavigationTab.STRATEGIES) },
                testTag = "nav_playbook"
            )
        }

        // Center Floating Action Button
        Box(
            modifier = Modifier
                .offset(y = (-26).dp)
                .size(56.dp)
                .shadow(12.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(AccentContainer)
                .border(3.dp, DarkBackground, RoundedCornerShape(16.dp))
                .clickable { onAddTradeClick() }
                .testTag("fab_add_trade"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Log Trade",
                tint = TextOnAccent,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun NavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 6.dp, vertical = 2.dp)
            .testTag(testTag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) AccentPrimary else TextMuted,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            color = if (isSelected) AccentPrimary else TextMuted,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}
