package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.AppNavScreen

data class NavItem(
    val screen: AppNavScreen,
    val label: String,
    val icon: ImageVector
)

@Composable
fun SisyaBottomNav(
    currentScreen: AppNavScreen,
    onNavigate: (AppNavScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavItem(AppNavScreen.DASHBOARD, "Home", Icons.Default.Home),
        NavItem(AppNavScreen.LEARN, "Learn", Icons.Default.School),
        NavItem(AppNavScreen.PROJECTS, "Projects", Icons.Default.Terminal),
        NavItem(AppNavScreen.PROVE, "Proof", Icons.Default.WorkspacePremium),
        NavItem(AppNavScreen.CAREER, "Career", Icons.Default.TrendingUp),
        NavItem(AppNavScreen.CHAT, "Chat", Icons.Default.ChatBubble)
    )

    Surface(
        color = SurfaceWhite,
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars),
        tonalElevation = 6.dp,
        border = androidx.compose.foundation.BorderStroke(0.5.dp, BorderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = currentScreen == item.screen
                val tint = if (isSelected) PrimaryDarkTeal else MutedText

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onNavigate(item.screen) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("nav_item_${item.screen.name.lowercase()}")
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) PrimarySoftTeal else androidx.compose.ui.graphics.Color.Transparent)
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = tint,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
                        ),
                        color = tint
                    )
                }
            }
        }
    }
}
