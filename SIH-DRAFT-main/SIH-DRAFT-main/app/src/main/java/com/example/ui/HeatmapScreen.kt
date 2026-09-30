package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class CivicTicket(
    val id: String,
    val category: String,
    val location: String,
    val district: String,
    val urgency: String,
    val slaHours: Int,
    val timeRemaining: String,
    val slaProgress: Float,
    val isVerified: Boolean,
    val fraudFlag: Boolean,
    val confidence: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeatmapScreen(
    modifier: Modifier = Modifier,
    onOpenInspector: () -> Unit = {}
) {
    var selectedViewMode by remember { mutableIntStateOf(0) } // 0: List View, 1: Heatmap Grid
    var selectedDistrict by remember { mutableStateOf("All Districts") }
    val districts = listOf("All Districts", "Hyderabad", "Warangal", "Karimnagar", "Nizamabad", "Rangareddy")
    var districtExpanded by remember { mutableStateOf(false) }

    val tickets = remember {
        listOf(
            CivicTicket(
                id = "CP-HYD-9921-X",
                category = "Roads & Infrastructure",
                location = "Begumpet Flyover, Secunderabad",
                district = "Hyderabad",
                urgency = "Critical",
                slaHours = 24,
                timeRemaining = "04h 12m",
                slaProgress = 0.82f,
                isVerified = true,
                fraudFlag = false,
                confidence = 96
            ),
            CivicTicket(
                id = "CP-WGL-8410-B",
                category = "Electrical & Power",
                location = "Hanamkonda Main Junction, Warangal",
                district = "Warangal",
                urgency = "High",
                slaHours = 48,
                timeRemaining = "18h 45m",
                slaProgress = 0.61f,
                isVerified = false,
                fraudFlag = true,
                confidence = 32
            ),
            CivicTicket(
                id = "CP-KMR-1029-A",
                category = "Water Supply",
                location = "Collectorate Road, Karimnagar",
                district = "Karimnagar",
                urgency = "Medium",
                slaHours = 72,
                timeRemaining = "41h 10m",
                slaProgress = 0.43f,
                isVerified = true,
                fraudFlag = false,
                confidence = 91
            ),
            CivicTicket(
                id = "CP-NZB-4491-C",
                category = "Sanitation",
                location = "Armoor Market Yard, Nizamabad",
                district = "Nizamabad",
                urgency = "Critical",
                slaHours = 24,
                timeRemaining = "02h 05m",
                slaProgress = 0.91f,
                isVerified = false,
                fraudFlag = false,
                confidence = 88
            )
        )
    }

    val filteredTickets = tickets.filter {
        selectedDistrict == "All Districts" || it.district == selectedDistrict
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header & View Switcher
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Public Grievance Heatmap",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnPrimaryContainer
                )
                Text(
                    text = "Live SLA Timers & Gemini Resolution Audit",
                    fontSize = 12.sp,
                    color = Slate500
                )
            }

            // View Toggle Switch
            SingleChoiceSegmentedButtonRow {
                SegmentedButton(
                    selected = selectedViewMode == 0,
                    onClick = { selectedViewMode = 0 },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) {
                    Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, modifier = Modifier.size(16.dp))
                }
                SegmentedButton(
                    selected = selectedViewMode == 1,
                    onClick = { selectedViewMode = 1 },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) {
                    Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }

        // District Filter & QA Inspector Launch Bar
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            ExposedDropdownMenuBox(
                expanded = districtExpanded,
                onExpandedChange = { districtExpanded = !districtExpanded },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = selectedDistrict,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Filter District") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = districtExpanded) },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary)
                )
                ExposedDropdownMenu(
                    expanded = districtExpanded,
                    onDismissRequest = { districtExpanded = false },
                    containerColor = Color.White,
                    modifier = Modifier.background(Color.White)
                ) {
                    districts.forEach { dist ->
                        DropdownMenuItem(
                            text = { Text(dist, color = OnPrimaryContainer, fontWeight = FontWeight.SemiBold, fontSize = 13.sp) },
                            onClick = {
                                selectedDistrict = dist
                                districtExpanded = false
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

            Button(
                onClick = onOpenInspector,
                modifier = Modifier.height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Icon(Icons.Default.Analytics, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("AI QA Inspector", fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
            }
        }

        if (selectedViewMode == 0) {
            // LIST VIEW
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                filteredTickets.forEach { ticket ->
                    CivicTicketCard(ticket = ticket, onOpenInspector = onOpenInspector)
                }
            }
        } else {
            // HEATMAP MAP VIEW SIMULATION
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
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Map, contentDescription = null, tint = Primary)
                            Text("Telangana Civic Heatmap Density", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = OnPrimaryContainer)
                        }
                        Box(
                            modifier = Modifier
                                .background(Red50, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("${filteredTickets.size} Active Hotspots", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Red700, maxLines = 1)
                        }
                    }

                    // Simulated Map Container
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BlueLightContainer, RoundedCornerShape(12.dp))
                            .border(1.dp, BlueLightBorder, RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("DISTRICT INCIDENCE & SLA PRESSURE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BlueDarkLabel)

                            HeatmapGridRow("Hyderabad Urban", count = 42, percentage = 0.85f, intensity = Red700)
                            HeatmapGridRow("Warangal Rural", count = 18, percentage = 0.45f, intensity = Red400)
                            HeatmapGridRow("Karimnagar North", count = 11, percentage = 0.25f, intensity = Green700)
                            HeatmapGridRow("Nizamabad West", count = 27, percentage = 0.65f, intensity = Red700)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CivicTicketCard(ticket: CivicTicket, onOpenInspector: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Slate100)
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
                    Text(ticket.id, fontWeight = FontWeight.Bold, fontSize = 13.sp, fontFamily = FontFamily.Monospace, color = OnPrimaryContainer)
                    Box(
                        modifier = Modifier
                            .background(Slate100, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(ticket.district, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Slate600, maxLines = 1)
                    }
                }

                // Urgency Badge
                val isUrgent = ticket.urgency == "Critical" || ticket.urgency == "High"
                Box(
                    modifier = Modifier
                        .background(if (isUrgent) Red50 else Green100, RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(ticket.urgency.uppercase(), fontSize = 10.sp, color = if (isUrgent) Red700 else Green700, fontWeight = FontWeight.Bold, maxLines = 1)
                }
            }

            Text(ticket.location, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Slate700)
            Text("Category: ${ticket.category}", fontSize = 11.sp, color = Slate500)

            // SLA Progress Bar
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(14.dp), tint = Primary)
                        Text("SLA: ${ticket.timeRemaining} left", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Primary, fontFamily = FontFamily.Monospace)
                    }
                    Text("Guaranteed SLA: ${ticket.slaHours}h", fontSize = 10.sp, color = Slate400)
                }
                LinearProgressIndicator(
                    progress = { ticket.slaProgress },
                    modifier = Modifier.fillMaxWidth().height(4.dp),
                    color = if (ticket.slaProgress > 0.8f) Red700 else Primary,
                    trackColor = Slate100
                )
            }

            HorizontalDivider(color = Slate100)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("AI RESOLUTION AUDIT:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Slate400)

                // AI Resolution Audit Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clickable { onOpenInspector() }
                        .background(
                            if (ticket.fraudFlag) Red50 else if (ticket.isVerified) Green100 else Slate100,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = if (ticket.fraudFlag) Icons.Default.Warning else if (ticket.isVerified) Icons.Default.CheckCircle else Icons.AutoMirrored.Filled.Help,
                        contentDescription = null,
                        tint = if (ticket.fraudFlag) Red700 else if (ticket.isVerified) Green700 else Slate500,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = if (ticket.fraudFlag) "FRAUD FLAG DETECTED (${ticket.confidence}%)" else if (ticket.isVerified) "AI VERIFIED PASSED (${ticket.confidence}%)" else "AUDIT PENDING",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (ticket.fraudFlag) Red700 else if (ticket.isVerified) Green700 else Slate600
                    )
                }
            }
        }
    }
}

@Composable
fun HeatmapGridRow(districtName: String, count: Int, percentage: Float, intensity: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.size(10.dp).background(intensity, CircleShape))
                Text(districtName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate700)
            }
            Text("$count Tickets", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Primary, fontFamily = FontFamily.Monospace)
        }
        LinearProgressIndicator(
            progress = { percentage },
            modifier = Modifier.fillMaxWidth().height(4.dp),
            color = intensity,
            trackColor = Color.White
        )
    }
}
