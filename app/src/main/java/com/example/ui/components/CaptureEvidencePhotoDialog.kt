package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.ui.theme.*
import com.example.util.PhotoStorageUtil

/**
 * Dialog allowing users to capture a photo using their device camera,
 * preview the captured snapshot, add title, category, and technical description,
 * and persist it into the local Room database as an immutable proof of work.
 */
@Composable
fun CaptureEvidencePhotoDialog(
    projectId: String,
    projectTitle: String,
    milestoneTitle: String? = null,
    taskNumber: Int? = null,
    onDismiss: () -> Unit,
    onSaveEvidence: (
        title: String,
        caption: String,
        imageUri: String,
        localFilePath: String?,
        category: String,
        tagsCsv: String
    ) -> Unit
) {
    val context = LocalContext.current

    var capturedTempUri by remember { mutableStateOf<Uri?>(null) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    var permanentUriString by remember { mutableStateOf<String?>(null) }
    var permanentFilePath by remember { mutableStateOf<String?>(null) }

    var title by remember { mutableStateOf("") }
    var caption by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("PROTOTYPE") }
    var tagsCsv by remember { mutableStateOf("engineering,prototype,verification") }
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val categories = listOf("PROTOTYPE", "WHITEBOARD", "CIRCUIT/HW", "BENCHMARK", "UI_TEST")

    // Launcher for taking picture with camera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && pendingCameraUri != null) {
            val uri = pendingCameraUri!!
            capturedTempUri = uri
            // Persist to internal storage for permanent offline retention
            val persisted = PhotoStorageUtil.persistPhotoToInternalStorage(context, uri)
            if (persisted != null) {
                permanentUriString = persisted.first
                permanentFilePath = persisted.second
            } else {
                permanentUriString = uri.toString()
            }
            if (title.isBlank()) {
                title = "Prototype Snapshot: ${selectedCategory.lowercase().replaceFirstChar { it.uppercase() }}"
            }
            Toast.makeText(context, "Photo captured successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    // Launcher for camera runtime permission
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (isGranted) {
            try {
                val tempUri = PhotoStorageUtil.createTempPictureUri(context)
                pendingCameraUri = tempUri
                cameraLauncher.launch(tempUri)
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to launch camera: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(
                context,
                "Camera permission is required to capture engineering evidence.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun launchCamera() {
        if (hasPermission) {
            try {
                val tempUri = PhotoStorageUtil.createTempPictureUri(context)
                pendingCameraUri = tempUri
                cameraLauncher.launch(tempUri)
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to launch camera: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f)
                .clip(RoundedCornerShape(20.dp)),
            color = SurfaceWhite,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = PrimaryDarkTeal,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Capture Engineering Evidence",
                                style = MaterialTheme.typography.titleLarge,
                                color = PrimaryText
                            )
                        }
                        Text(
                            text = "Associate physical prototype / whiteboard proof with $projectTitle",
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryText
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_camera_dialog")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = SecondaryText)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Camera viewfinder / Image Preview Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(SecondarySurface)
                        .border(1.dp, PrimaryDarkTeal.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .clickable { launchCamera() }
                        .testTag("preview_camera_box"),
                    contentAlignment = Alignment.Center
                ) {
                    if (permanentUriString != null || capturedTempUri != null) {
                        val displayUri = permanentUriString ?: capturedTempUri.toString()
                        AsyncImage(
                            model = displayUri,
                            contentDescription = "Captured Evidence Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        // Retake overlay button
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(12.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.Black.copy(alpha = 0.65f))
                                .clickable { launchCamera() }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.PhotoCamera,
                                    contentDescription = "Retake",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Retake Photo",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(PrimarySoftTeal),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Open Camera",
                                    tint = PrimaryDarkTeal,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Tap to Launch Camera",
                                style = MaterialTheme.typography.titleMedium,
                                color = PrimaryDarkTeal
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Snap your circuit board, whiteboard architecture, or hardware prototype",
                                style = MaterialTheme.typography.bodySmall,
                                color = SecondaryText
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Category selector chips
                Text(
                    text = "Evidence Category:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = PrimaryText
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = cat },
                            label = {
                                Text(
                                    text = cat,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimarySoftTeal,
                                selectedLabelColor = PrimaryDarkTeal
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Evidence Title") },
                    placeholder = { Text("e.g. Distributed Lock Redis Benchmark / Circuit Prototype") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_evidence_title"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryDarkTeal,
                        cursorColor = PrimaryDarkTeal
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Description/Caption Input
                OutlinedTextField(
                    value = caption,
                    onValueChange = { caption = it },
                    label = { Text("Technical Description & Engineering Rationale") },
                    placeholder = { Text("Detail what physical/architectural proof this photo verifies...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("input_evidence_caption"),
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryDarkTeal,
                        cursorColor = PrimaryDarkTeal
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Associated task & milestone indicator
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = SecondarySurface
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderSpecial,
                            contentDescription = null,
                            tint = PrimaryDarkTeal,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Project: $projectTitle",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = PrimaryText
                            )
                            Text(
                                text = "Milestone: ${milestoneTitle ?: "Core Architecture"} • Task: #${taskNumber ?: 12}",
                                style = MaterialTheme.typography.bodySmall,
                                color = SecondaryText
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_cancel_evidence")
                    ) {
                        Text("Cancel", color = SecondaryText)
                    }

                    Button(
                        onClick = {
                            val uriToSave = permanentUriString ?: capturedTempUri?.toString() ?: ""
                            if (uriToSave.isBlank()) {
                                Toast.makeText(
                                    context,
                                    "Please capture a photo first using the camera.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }
                            onSaveEvidence(
                                title.ifBlank { "Prototype Snapshot: $selectedCategory" },
                                caption.ifBlank { "Physical evidence entry captured via device camera." },
                                uriToSave,
                                permanentFilePath,
                                selectedCategory,
                                tagsCsv
                            )
                            Toast.makeText(context, "Evidence saved to Project Database!", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("btn_save_photo_evidence"),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryDarkTeal)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save to Ledger", color = SurfaceWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
