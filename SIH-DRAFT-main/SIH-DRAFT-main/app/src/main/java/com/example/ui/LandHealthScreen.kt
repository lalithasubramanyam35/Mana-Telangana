package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandHealthScreen(modifier: Modifier = Modifier) {
    var khataNo by remember { mutableStateOf("40192") }
    var surveyNo by remember { mutableStateOf("142/A") }
    var selectedDharaniIssue by remember { mutableStateOf("Extent Mismatch") }
    var dharaniSubmitted by remember { mutableStateOf(false) }

    var hospitalQuery by remember { mutableStateOf("") }
    var selectedHospitalType by remember { mutableStateOf("All Empanelled Hospitals") }

    val dharaniIssues = listOf(
        "Extent Mismatch",
        "Prohibited Land List (22A) Removal",
        "Pattadar Name Spelling Correction",
        "Land Mutation & Sub-Division Delay"
    )
    var issueExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Land & Health Telangana",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnPrimaryContainer
                )
                Text(
                    text = "Dharani Portal 2.0 & Unified Aarogyasri Health Network",
                    fontSize = 11.sp,
                    color = Slate500
                )
            }
            Box(
                modifier = Modifier
                    .background(BlueLightContainer, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("Dharani 2.0 • Aarogyasri", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Primary, maxLines = 1)
            }
        }

        // Card 1: 1-Click Dharani Land Record Corrections
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(PrimaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Landscape, contentDescription = null, tint = Primary)
                    }
                    Column {
                        Text("1-Click Dharani Record Corrections", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = OnPrimaryContainer)
                        Text("Telangana Revenue Land Portal Instant Redressal", fontSize = 11.sp, color = Slate500)
                    }
                }

                HorizontalDivider(color = Slate100)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = khataNo,
                        onValueChange = { khataNo = it },
                        label = { Text("Khata / Passbook No.") },
                        placeholder = { Text("e.g. 40192") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary)
                    )
                    OutlinedTextField(
                        value = surveyNo,
                        onValueChange = { surveyNo = it },
                        label = { Text("Survey No.") },
                        placeholder = { Text("e.g. 142/A") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary)
                    )
                }

                ExposedDropdownMenuBox(
                    expanded = issueExpanded,
                    onExpandedChange = { issueExpanded = !issueExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedDharaniIssue,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Correction Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = issueExpanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary)
                    )
                    ExposedDropdownMenu(
                        expanded = issueExpanded,
                        onDismissRequest = { issueExpanded = false },
                        containerColor = Color.White,
                        modifier = Modifier.background(Color.White)
                    ) {
                        dharaniIssues.forEach { issue ->
                            DropdownMenuItem(
                                text = { Text(issue, color = OnPrimaryContainer, fontWeight = FontWeight.SemiBold, fontSize = 13.sp) },
                                onClick = {
                                    selectedDharaniIssue = issue
                                    issueExpanded = false
                                },
                                colors = MenuDefaults.itemColors(
                                    textColor = OnPrimaryContainer,
                                    leadingIconColor = OnPrimaryContainer
                                ),
                                modifier = Modifier.background(Color.White)
                            )
                        }
                    }
                }

                AnimatedVisibility(visible = dharaniSubmitted) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Green50, RoundedCornerShape(12.dp))
                            .border(1.dp, Green100, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Green700, modifier = Modifier.size(16.dp))
                            Text("DHARANI CORRECTION DISPATCHED", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Green700)
                        }
                        Text("Token: DHR-TS-2026-99410 | Collectorate Assigned: Rangareddy", fontSize = 12.sp, color = Slate700)
                        Text("SLA Resolution Guarantee: 7 working days", fontSize = 11.sp, color = Slate600)
                        LinearProgressIndicator(progress = { 0.15f }, modifier = Modifier.fillMaxWidth().height(4.dp), color = Green700, trackColor = Green100)
                    }
                }

                Button(
                    onClick = { dharaniSubmitted = true },
                    enabled = khataNo.isNotBlank() && surveyNo.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("File 1-Click Dharani Grievance", fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                }
            }
        }

        // Card 2: Unified Aarogyasri Health Wallet
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Red50, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.HealthAndSafety, contentDescription = null, tint = Red700)
                        }
                        Column {
                            Text("Unified Aarogyasri Wallet", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = OnPrimaryContainer)
                            Text("Card No: TS-AAR-8829104", fontSize = 11.sp, color = Slate500, fontFamily = FontFamily.Monospace)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .background(Red50, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("₹ 10.0 Lakhs Limit", fontSize = 10.sp, color = Red700, fontWeight = FontWeight.Bold, maxLines = 1)
                    }
                }

                HorizontalDivider(color = Slate100)

                // Premium Metallic Digital Health Card Visual
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(listOf(Slate900, Slate800)),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .border(1.dp, GoldBorder, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("TELANGANA AAROGYASRI HEALTH CARD", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GoldBorder, letterSpacing = 0.5.sp)
                            Icon(Icons.Default.Contactless, contentDescription = null, tint = GoldBorder, modifier = Modifier.size(16.dp))
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("AVAILABLE BALANCE", fontSize = 8.sp, color = Slate400)
                                Text("₹ 9,75,500.00", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White, fontFamily = FontFamily.Monospace)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("PRIMARY BENEFICIARY", fontSize = 8.sp, color = Slate400)
                                Text("K. Srinivas", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                        LinearProgressIndicator(progress = { 0.97f }, modifier = Modifier.fillMaxWidth().height(4.dp), color = Green600, trackColor = Slate700)
                    }
                }

                // Search Empanelled Hospitals
                OutlinedTextField(
                    value = hospitalQuery,
                    onValueChange = { hospitalQuery = it },
                    placeholder = { Text("Search empanelled hospital (e.g. NIMS, Yashoda, Gandhi)...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Slate400) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Red700)
                )

                // Claims Timeline
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("PRE-AUTHORIZATION & RECENT CLAIMS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Slate400, letterSpacing = 0.5.sp)

                    HospitalClaimItem(
                        hospitalName = "NIMS Hyderabad (Panjagutta)",
                        procedure = "Nephrology Procedure / Dialysis",
                        distance = "2.4 km",
                        amount = "₹ 24,500",
                        status = "Pre-Auth Approved",
                        isApproved = true
                    )

                    HospitalClaimItem(
                        hospitalName = "Yashoda Hospital (Secunderabad)",
                        procedure = "Cardiology Diagnostic Panel",
                        distance = "4.1 km",
                        amount = "₹ 18,000",
                        status = "Claim Settled",
                        isApproved = true
                    )
                }
            }
        }
    }
}

@Composable
fun HospitalClaimItem(
    hospitalName: String,
    procedure: String,
    distance: String,
    amount: String,
    status: String,
    isApproved: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Slate50, RoundedCornerShape(10.dp))
            .border(1.dp, Slate100, RoundedCornerShape(10.dp))
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(hospitalName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = OnPrimaryContainer)
                Box(
                    modifier = Modifier
                        .background(BlueLightContainer, RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(distance, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Primary)
                }
            }
            Text(procedure, fontSize = 11.sp, color = Slate500)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(amount, fontWeight = FontWeight.Bold, fontSize = 12.sp, fontFamily = FontFamily.Monospace, color = Primary)
            Text(
                status,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isApproved) Green700 else Red700
            )
        }
    }
}
