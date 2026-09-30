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
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.Send
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PensionMeeSevaScreen(modifier: Modifier = Modifier) {
    var empId by remember { mutableStateOf("TS109842") }
    var selectedTreasury by remember { mutableStateOf("0102 - Hyderabad Urban") }
    var showPayslipDialog by remember { mutableStateOf(false) }
    var showGpfDialog by remember { mutableStateOf(false) }
    var selectedMeeSevaService by remember { mutableStateOf("Income Certificate") }
    var autoFillStatus by remember { mutableStateOf(false) }
    var isScanningFace by remember { mutableStateOf(false) }
    var lifeCertStatus by remember { mutableStateOf("Verified via Aadhaar STQC (Valid till Nov 2026)") }

    val treasuryOptions = listOf(
        "0102 - Hyderabad Urban",
        "0201 - Ranga Reddy District",
        "0305 - Warangal District",
        "0402 - Karimnagar District",
        "0501 - Nizamabad District"
    )
    var treasuryExpanded by remember { mutableStateOf(false) }

    val meeSevaServices = listOf(
        "Income Certificate",
        "Caste & Community Certificate",
        "Residence Certificate",
        "Revenue Encumbrance Certificate"
    )
    var serviceExpanded by remember { mutableStateOf(false) }

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
                    text = "Pension & MeeSeva Portal",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnPrimaryContainer
                )
                Text(
                    text = "Telangana Digital Services & Pension Gateway",
                    fontSize = 11.sp,
                    color = Slate500
                )
            }
            Box(
                modifier = Modifier
                    .background(PrimaryContainer, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("IFMIS • STQC Certified", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Primary, maxLines = 1)
            }
        }

        // Card 1: IFMIS Payslip & Pension Request
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
                        Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null, tint = Primary)
                    }
                    Column {
                        Text("IFMIS Payslip & Pension Portal", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = OnPrimaryContainer)
                        Text("Government of Telangana Employee & Pensioner Portal", fontSize = 11.sp, color = Slate500)
                    }
                }

                HorizontalDivider(color = Slate100)

                OutlinedTextField(
                    value = empId,
                    onValueChange = { empId = it },
                    label = { Text("Employee / Pensioner ID") },
                    placeholder = { Text("Enter 8-digit IFMIS ID") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary)
                )

                // Dropdown menu with white container and dark text fix
                ExposedDropdownMenuBox(
                    expanded = treasuryExpanded,
                    onExpandedChange = { treasuryExpanded = !treasuryExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedTreasury,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Treasury / Sub-Treasury Code") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = treasuryExpanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary)
                    )
                    ExposedDropdownMenu(
                        expanded = treasuryExpanded,
                        onDismissRequest = { treasuryExpanded = false },
                        containerColor = Color.White,
                        modifier = Modifier.background(Color.White)
                    ) {
                        treasuryOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option, color = OnPrimaryContainer, fontWeight = FontWeight.SemiBold, fontSize = 13.sp) },
                                onClick = {
                                    selectedTreasury = option
                                    treasuryExpanded = false
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

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { showPayslipDialog = true },
                        enabled = empId.isNotBlank(),
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Fetch Payslip", fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    }

                    OutlinedButton(
                        onClick = { showGpfDialog = true },
                        enabled = empId.isNotBlank(),
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Primary)
                    ) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("GPF Info", fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    }
                }
            }
        }

        // Card 2: Jeevan Pramaan Digital Life Certificate
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
                            .background(Green100, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Green700)
                    }
                    Column {
                        Text("Jeevan Pramaan Digital Life Cert", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = OnPrimaryContainer)
                        Text("Aadhaar STQC Face Authentication & Biometrics", fontSize = 11.sp, color = Slate500)
                    }
                }

                HorizontalDivider(color = Slate100)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Green100.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .border(1.dp, Green700.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("CERTIFICATE STATUS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Green700)
                        Text(lifeCertStatus, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                    }
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Green600)
                }

                // Simulated Scanner Viewfinder
                AnimatedVisibility(visible = isScanningFace) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Slate900, RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.CameraFront, contentDescription = null, tint = Green600, modifier = Modifier.size(44.dp))
                        Text("Scanning Face via STQC Aadhaar Engine...", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth().height(4.dp), color = Green600, trackColor = Slate700)
                    }
                }

                Button(
                    onClick = {
                        isScanningFace = true
                        lifeCertStatus = "Re-verified via STQC FaceAuth (Valid till Nov 2027)"
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Green700)
                ) {
                    Icon(Icons.Default.CameraFront, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(if (isScanningFace) "Scanning Biometrics..." else "Trigger Biometric / FaceAuth Scan", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        // Card 3: MeeSeva Auto-Fill Gateway
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
                            .background(PurpleLightContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PurpleDarkLabel)
                    }
                    Column {
                        Text("MeeSeva Auto-Fill Gateway", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = OnPrimaryContainer)
                        Text("1-Click Verified Citizen Data Pre-Population", fontSize = 11.sp, color = Slate500)
                    }
                }

                HorizontalDivider(color = Slate100)

                ExposedDropdownMenuBox(
                    expanded = serviceExpanded,
                    onExpandedChange = { serviceExpanded = !serviceExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedMeeSevaService,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Target MeeSeva Application") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = serviceExpanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PurpleDarkLabel)
                    )
                    ExposedDropdownMenu(
                        expanded = serviceExpanded,
                        onDismissRequest = { serviceExpanded = false },
                        containerColor = Color.White,
                        modifier = Modifier.background(Color.White)
                    ) {
                        meeSevaServices.forEach { service ->
                            DropdownMenuItem(
                                text = { Text(service, color = OnPrimaryContainer, fontWeight = FontWeight.SemiBold, fontSize = 13.sp) },
                                onClick = {
                                    selectedMeeSevaService = service
                                    serviceExpanded = false
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

                AnimatedVisibility(visible = autoFillStatus) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(PurpleLightContainer, RoundedCornerShape(12.dp))
                            .border(1.dp, PurpleLightBorder, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = PurpleDarkLabel, modifier = Modifier.size(16.dp))
                            Text("PRE-FILLED CITIZEN DATA (DIGILOCKER VERIFIED)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PurpleDarkLabel)
                        }
                        Text("Name: K. Srinivas", fontSize = 12.sp, color = Slate700, fontWeight = FontWeight.Medium)
                        Text("Designation: GHM Gr-I", fontSize = 12.sp, color = Slate700, fontWeight = FontWeight.Bold)
                        Text("Aadhaar: XXXX-XXXX-4819 | Ration Card: WAP36040192", fontSize = 12.sp, color = Slate700)
                        Text("Mandal: Khairatabad | District: Hyderabad", fontSize = 12.sp, color = Slate700)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { autoFillStatus = !autoFillStatus },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, PurpleDarkLabel),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(16.dp), tint = PurpleDarkLabel)
                        Spacer(Modifier.width(6.dp))
                        Text(if (autoFillStatus) "Clear Auto-Fill" else "Auto-Fill Data", fontSize = 12.sp, color = PurpleDarkLabel, fontWeight = FontWeight.Bold, maxLines = 1)
                    }

                    Button(
                        onClick = { /* Submit application */ },
                        enabled = autoFillStatus,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PurpleDarkLabel),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Submit Gateway", fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    }
                }
            }
        }
    }

    // Payslip Modal Preview with K. Srinivas, GHM Gr-I, and ₹ 1,64,450 salary
    if (showPayslipDialog) {
        AlertDialog(
            onDismissRequest = { showPayslipDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Receipt, tint = Primary, contentDescription = null)
                    Text("IFMIS Payslip Preview", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Employee: K. Srinivas ($empId)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = OnPrimaryContainer)
                    Text("Post: GHM Gr-I | Treasury: $selectedTreasury", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Primary)
                    HorizontalDivider(color = Slate200)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Basic Pay:", fontSize = 12.sp)
                        Text("₹ 98,770.00", fontWeight = FontWeight.Bold, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    }
                    LinearProgressIndicator(progress = { 0.6f }, modifier = Modifier.fillMaxWidth().height(4.dp), color = Primary, trackColor = Slate100)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("DA (42.5%):", fontSize = 12.sp)
                        Text("₹ 41,977.00", fontWeight = FontWeight.Bold, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    }
                    LinearProgressIndicator(progress = { 0.25f }, modifier = Modifier.fillMaxWidth().height(4.dp), color = CyberBlue, trackColor = Slate100)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("HRA (24%):", fontSize = 12.sp)
                        Text("₹ 23,703.00", fontWeight = FontWeight.Bold, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    }
                    LinearProgressIndicator(progress = { 0.15f }, modifier = Modifier.fillMaxWidth().height(4.dp), color = TelanganaGold, trackColor = Slate100)

                    HorizontalDivider(color = Slate200)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Net Salary:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Primary)
                        Text("₹ 1,64,450.00", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Primary, fontFamily = FontFamily.Monospace)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPayslipDialog = false }) {
                    Text("Close Preview", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Realtime GPF Modal Statement
    if (showGpfDialog) {
        AlertDialog(
            onDismissRequest = { showGpfDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.AccountBalanceWallet, tint = Primary, contentDescription = null)
                    Text("Realtime GPF Statement", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Subscriber: K. Srinivas ($empId)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = OnPrimaryContainer)
                    Text("Designation: GHM Gr-I | Joined Service: 12-Jul-2001", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Primary)
                    Text("GPF Account No: TS/EDN/401928", fontSize = 11.sp, color = Slate500, fontFamily = FontFamily.Monospace)
                    HorizontalDivider(color = Slate200)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Opening Balance (01-Apr-2025):", fontSize = 12.sp)
                        Text("₹ 42,80,000.00", fontWeight = FontWeight.Bold, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Annual Subscription (2025-26):", fontSize = 12.sp)
                        Text("+ ₹ 2,80,000.00", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Green700, fontFamily = FontFamily.Monospace)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Govt Interest (7.1% p.a.):", fontSize = 12.sp)
                        Text("+ ₹ 3,05,240.00", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TelanganaGold, fontFamily = FontFamily.Monospace)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Advances / Withdrawals:", fontSize = 12.sp)
                        Text("- ₹ 0.00", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate400, fontFamily = FontFamily.Monospace)
                    }

                    HorizontalDivider(color = Slate200)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Closing GPF Balance (24 Yrs):", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Primary)
                        Text("₹ 48,65,240.00", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Primary, fontFamily = FontFamily.Monospace)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Green50, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text("✔ Realtime AG Telangana Treasury Sync Active", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Green700)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showGpfDialog = false }) {
                    Text("Close Statement", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
