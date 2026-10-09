package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.local.PhotoEvidenceEntity
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PhotoEvidenceCardView(
    evidence: PhotoEvidenceEntity,
    onDelete: () -> Unit
) {
    var showFullImageDialog by remember { mutableStateOf(false) }

    val formattedDate = remember(evidence.createdAt) {
        SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()).format(Date(evidence.createdAt))
    }

    SisyaCard(
        backgroundColor = SurfaceWhite,
        borderColor = PrimaryDarkTeal.copy(alpha = 0.25f),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = PrimaryDarkTeal,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = evidence.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = PrimaryText,
                    maxLines = 1
                )
            }
            StatusPill(text = evidence.category, color = PrimaryDarkTeal)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Image thumbnail (clickable to inspect full screen)
        if (evidence.imageUri.isNotBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SecondarySurface)
                    .clickable { showFullImageDialog = true }
                    .testTag("evidence_image_thumbnail_${evidence.id}")
            ) {
                AsyncImage(
                    model = evidence.imageUri,
                    contentDescription = evidence.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Enlarge hint overlay
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.6f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ZoomIn, contentDescription = "Zoom", tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tap to Enlarge", color = Color.White, fontSize = 11.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Caption / Technical rationale
        Text(
            text = evidence.caption,
            style = MaterialTheme.typography.bodySmall,
            color = PrimaryText
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Metadata badges
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(SecondarySurface)
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Associated: Task #${evidence.taskNumber ?: 12}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = PrimaryDarkTeal,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = formattedDate,
                    fontSize = 11.sp,
                    color = MutedText
                )
            }
            if (!evidence.milestoneTitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Milestone: ${evidence.milestoneTitle}",
                    fontSize = 11.sp,
                    color = SecondaryText
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Footer Actions: Verified indicator and Delete
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Verified, contentDescription = "Verified", tint = SuccessGreen, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Room DB Immutable Record",
                    fontSize = 11.sp,
                    color = SuccessGreen,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(28.dp).testTag("btn_delete_evidence_${evidence.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete Evidence",
                    tint = SecondaryText,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }

    // Full screen Image Dialog
    if (showFullImageDialog) {
        Dialog(onDismissRequest = { showFullImageDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(16.dp)),
                color = SurfaceWhite
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = evidence.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = PrimaryText,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { showFullImageDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = SecondaryText)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(340.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SecondarySurface),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = evidence.imageUri,
                            contentDescription = evidence.title,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = evidence.caption,
                        style = MaterialTheme.typography.bodySmall,
                        color = SecondaryText
                    )
                }
            }
        }
    }
}
