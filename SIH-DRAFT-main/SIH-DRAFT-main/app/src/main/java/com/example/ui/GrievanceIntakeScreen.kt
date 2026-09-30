package com.example.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrievanceIntakeScreen(
    modifier: Modifier = Modifier,
    viewModel: GrievanceViewModel = viewModel()
) {
    var textInput by remember { mutableStateOf("") }
    var isRecordingAudio by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()
    val selectedTier by viewModel.selectedVerificationTier.collectAsState()
    val photoUri by viewModel.photoEvidenceUri.collectAsState()
    val gpsLocation by viewModel.gpsLocationTag.collectAsState()

    val photoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        viewModel.setPhotoEvidence(uri)
    }

    val samplePrompts = listOf(
        "⚡ Road Pothole at Begumpet Flyover causing heavy traffic congestion",
        "⚡ 3-Phase Low Voltage & Frequent Outages in Khairatabad Ward 92",
        "⚡ Illegal Sewage Overflow near Collectorate Office, Warangal",
        "⚡ Vigilance: Officer demanding illegal bribe of ₹5,000 for land mutation"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero AI Intake Status Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BlueLightContainer),
            border = BorderStroke(1.dp, BlueLightBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Primary, modifier = Modifier.size(16.dp))
                        Text("GEMINI 3.5 INTAKE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BlueDarkLabel, maxLines = 1)
                    }
                    Spacer(Modifier.height(2.dp))
                    Text("Case ID: CP-HYD-2026-X99", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OnPrimaryContainer, fontFamily = FontFamily.Monospace, maxLines = 1)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("CONFIDENCE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BlueDarkLabel, maxLines = 1)
                    Text(
                        text = if (uiState is GrievanceUiState.Success) {
                            "${((uiState as GrievanceUiState.Success).analysis.confidence_score * 100).toInt()}%"
                        } else "0.0%",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1
                    )
                }
            }
        }

        // Section 1: Verification Tier Selector with Security Cards
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                    Text("CITIZEN VERIFICATION TIER", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Slate400, letterSpacing = 0.5.sp)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    VerificationTier.values().forEach { tier ->
                        val isSelected = selectedTier == tier
                        OutlinedButton(
                            onClick = { viewModel.setVerificationTier(tier) },
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, if (isSelected) Primary else Slate200),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSelected) PrimaryContainer.copy(alpha = 0.4f) else Slate50,
                                contentColor = if (isSelected) Primary else Slate600
                            ),
                            contentPadding = PaddingValues(horizontal = 2.dp)
                        ) {
                            Text(
                                text = when (tier) {
                                    VerificationTier.MOBILE_OTP -> "Mobile OTP"
                                    VerificationTier.DIGILOCKER_EKYC -> "DigiLocker"
                                    VerificationTier.WHISTLEBLOWER -> "Whistleblower"
                                },
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                maxLines = 1
                            )
                        }
                    }
                }

                AnimatedVisibility(visible = selectedTier == VerificationTier.WHISTLEBLOWER) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Red50, RoundedCornerShape(10.dp))
                            .border(1.dp, Red100, RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = Red700, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text("Zero-Knowledge Anti-Corruption Protection Active", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Red700)
                            Text("Metadata stripped. Grievance auto-routed directly to Telangana State Vigilance Commission.", fontSize = 10.sp, color = Slate600)
                        }
                    }
                }
            }
        }

        // Section 2: Photo Evidence & GPS Location Stamp
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                        Text("PHOTO EVIDENCE & GPS STAMP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Slate400, letterSpacing = 0.5.sp)
                    }
                    if (photoUri != null) {
                        Box(
                            modifier = Modifier
                                .background(Green100, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("GPS Verified", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Green700)
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = { photoLauncher.launch("image/*") },
                        modifier = Modifier.height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Slate200),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Slate50)
                    ) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp), tint = Primary)
                        Spacer(Modifier.width(6.dp))
                        Text(if (photoUri != null) "Change Photo" else "Upload Evidence", fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    }

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .background(Green50, RoundedCornerShape(10.dp))
                            .border(1.dp, Green100, RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.MyLocation, contentDescription = null, tint = Green700, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(gpsLocation, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Slate700, maxLines = 2)
                    }
                }
            }
        }

        // Section 3: Voice / Text Complaint Input Box
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(16.dp))
                .border(1.dp, Slate100, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (uiState !is GrievanceUiState.Success) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Translate, contentDescription = null, modifier = Modifier.size(16.dp), tint = Slate400)
                        Text("UNSTRUCTURED COMPLAINT INPUT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Slate400, letterSpacing = 0.5.sp)
                    }

                    // Voice Input Simulator Toggle Button
                    IconButton(
                        onClick = {
                            isRecordingAudio = !isRecordingAudio
                            if (isRecordingAudio) {
                                textInput = "హైదరాబాద్ బేగంపేట్ ఫ్లైఓవర్ దగ్గర పెద్ద గోతులు పడి ట్రాఫిక్ చాలా ఇబ్బందిగా ఉంది"
                            }
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .background(if (isRecordingAudio) Red50 else Slate100, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isRecordingAudio) Icons.Default.Stop else Icons.Default.Mic,
                            contentDescription = "Voice Input",
                            tint = if (isRecordingAudio) Red700 else Primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Audio Visualizer Simulation Bar
                AnimatedVisibility(visible = isRecordingAudio) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Red50, RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🔴 Recording Voice Input (Telugu/English)...", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Red700)
                        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            repeat(5) { index ->
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height((12 + index * 4).dp)
                                        .background(Red700, RoundedCornerShape(2.dp))
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 130.dp),
                    placeholder = { Text("Speak or type in Telugu, Hindi, Tamil, or English...\ne.g., 'Low voltage issue in Khairatabad Ward 92' or 'Bribe requested at MRO office'", color = Slate400, fontSize = 13.sp) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Slate50,
                        focusedContainerColor = Slate50,
                        unfocusedBorderColor = Slate200,
                        focusedBorderColor = Primary
                    )
                )

                // Quick Prompt Chips
                Text("QUICK SAMPLE COMPLAINTS (TAP TO TEST)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Slate400, letterSpacing = 0.5.sp)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(samplePrompts) { prompt ->
                        SuggestionChip(
                            onClick = { textInput = prompt },
                            label = { Text(prompt.take(32) + "...", fontSize = 11.sp) },
                            colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Slate50)
                        )
                    }
                }
            } else {
                // GEMINI AI RESULT VIEW
                val analysis = (uiState as GrievanceUiState.Success).analysis
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp), tint = Primary)
                        Text("GEMINI STRUCTURED ANALYSIS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Primary, letterSpacing = 0.5.sp)
                    }
                    Box(
                        modifier = Modifier
                            .background(Slate100, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            "LANG: ${analysis.language_detected.take(5).uppercase()}", 
                            fontSize = 10.sp, 
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = Slate700
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Slate50, RoundedCornerShape(12.dp))
                ) {
                    Box(modifier = Modifier.width(4.dp).height(IntrinsicSize.Min).background(Primary, RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)))
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("\"$textInput\"", fontSize = 13.sp, color = Slate700, fontStyle = FontStyle.Italic, lineHeight = 20.sp)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Slate200)
                        Text(analysis.raw_summary, fontSize = 14.sp, color = OnPrimaryContainer, fontWeight = FontWeight.Medium, lineHeight = 22.sp)
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        DataGridItem("Category", analysis.category, modifier = Modifier.weight(1f))
                        DataGridItem("Urgency Level", analysis.urgency_score, modifier = Modifier.weight(1f), isUrgent = analysis.urgency_score == "High" || analysis.urgency_score == "Critical")
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        DataGridItem("Target Department", analysis.target_department_code, modifier = Modifier.weight(1f), isMono = true)
                        DataGridItem("Guaranteed SLA", "${analysis.suggested_sla_hours} Hours", modifier = Modifier.weight(1f))
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PurpleLightContainer, RoundedCornerShape(12.dp))
                        .border(1.dp, PurpleLightBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text("ACTIONABLE GEOGRAPHIC LOCATION", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = PurpleDarkLabel, letterSpacing = 0.5.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(analysis.actionable_location_text.ifBlank { gpsLocation }, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = PurpleDarkText)
                }
            }

            if (uiState is GrievanceUiState.Error) {
                Text(
                    text = "Error: ${(uiState as GrievanceUiState.Error).message}",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Section 4: Action Buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState is GrievanceUiState.Success) {
                OutlinedButton(
                    onClick = { /* Reset/Edit */ },
                    modifier = Modifier.weight(1f).height(48.dp),
                    border = BorderStroke(1.dp, Primary),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Edit Input", fontWeight = FontWeight.Bold, maxLines = 1)
                }

                Button(
                    onClick = { /* Dispatch ticket */ },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = Color.White)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Dispatch Ticket", fontWeight = FontWeight.Bold, maxLines = 1)
                }
            } else {
                Button(
                    onClick = { viewModel.analyze(textInput) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    enabled = textInput.isNotBlank() && uiState !is GrievanceUiState.Loading,
                    colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = Color.White)
                ) {
                    if (uiState is GrievanceUiState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Analytics, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Process Grievance via Gemini AI", fontWeight = FontWeight.Bold, maxLines = 1)
                    }
                }
            }
        }
    }
}

@Composable
fun DataGridItem(label: String, value: String, modifier: Modifier = Modifier, isUrgent: Boolean = false, isMono: Boolean = false) {
    val bgColor = if (isUrgent) Red50 else Slate50
    val borderColor = if (isUrgent) Red100 else Slate100
    val labelColor = if (isUrgent) Red700 else Slate400
    val valueColor = if (isUrgent) Red700 else if (isMono) Slate700 else Primary

    Column(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(10.dp))
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Text(label.uppercase(), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = labelColor, letterSpacing = 0.5.sp)
        Spacer(Modifier.height(4.dp))
        Text(
            value.ifBlank { "N/A" }, 
            fontSize = if (isMono) 11.sp else 12.sp, 
            fontWeight = FontWeight.Bold, 
            color = valueColor,
            fontFamily = if (isMono) FontFamily.Monospace else FontFamily.Default,
            maxLines = 1
        )
    }
}
