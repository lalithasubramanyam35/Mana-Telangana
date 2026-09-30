package com.example.ui.auth

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

enum class IdentityType { AADHAAR, PAN }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    var phoneInput by remember { mutableStateOf("") }
    var selectedIdentityType by remember { mutableStateOf(IdentityType.AADHAAR) }
    var identityNumberInput by remember { mutableStateOf("") }
    var captchaInput by remember { mutableStateOf("") }

    val authState by viewModel.authState.collectAsState()
    val captchaCode by viewModel.captchaCode.collectAsState()

    val isAadhaar = selectedIdentityType == IdentityType.AADHAAR
    val isPhoneValid = phoneInput.length == 10
    val isIdentityValid = if (isAadhaar) identityNumberInput.length == 12 else identityNumberInput.length == 10
    val isCaptchaValid = captchaInput.length == 5
    val isFormValid = isPhoneValid && isIdentityValid && isCaptchaValid

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(10.dp))

        // Branding & Header Logo
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(
                    brush = Brush.linearGradient(listOf(Primary, CyberBlue)),
                    shape = RoundedCornerShape(18.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Analytics,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(34.dp)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "CivicPulse Telangana",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnPrimaryContainer,
                    letterSpacing = (-0.5).sp
                )
                Box(
                    modifier = Modifier
                        .background(GoldLight, RoundedCornerShape(6.dp))
                        .border(1.dp, GoldBorder, RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("eKYC Portal", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TelanganaGold)
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Government of Telangana Single Sign-On Gateway",
                fontSize = 11.sp,
                color = Slate500,
                fontWeight = FontWeight.Medium
            )
        }

        // Login Card Container
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Primary, modifier = Modifier.size(20.dp))
                    Text(
                        text = "Citizen Authentication",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnPrimaryContainer
                    )
                }

                HorizontalDivider(color = Slate100)

                // 1. Mandatory Mobile Number Field
                OutlinedTextField(
                    value = phoneInput,
                    onValueChange = { if (it.length <= 10 && it.all { char -> char.isDigit() }) phoneInput = it },
                    label = { Text("Mobile Number (Mandatory)") },
                    placeholder = { Text("Enter 10-digit registered mobile") },
                    leadingIcon = { Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = Primary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary)
                )

                // 2. Identity Type Selector: Aadhaar vs PAN
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "SELECT E-KYC IDENTIFIER",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate400,
                        letterSpacing = 0.5.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                selectedIdentityType = IdentityType.AADHAAR
                                identityNumberInput = ""
                            },
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, if (isAadhaar) Primary else Slate200),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isAadhaar) PrimaryContainer.copy(alpha = 0.4f) else Slate50,
                                contentColor = if (isAadhaar) Primary else Slate600
                            ),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            Icon(Icons.Default.Badge, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Aadhaar (12 Digits)", fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        }

                        OutlinedButton(
                            onClick = {
                                selectedIdentityType = IdentityType.PAN
                                identityNumberInput = ""
                            },
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, if (!isAadhaar) Primary else Slate200),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (!isAadhaar) PrimaryContainer.copy(alpha = 0.4f) else Slate50,
                                contentColor = if (!isAadhaar) Primary else Slate600
                            ),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            Icon(Icons.Default.CreditCard, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("PAN (10 Chars)", fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        }
                    }
                }

                // 3. Dynamic Aadhaar / PAN Input Field
                OutlinedTextField(
                    value = identityNumberInput,
                    onValueChange = { input ->
                        if (isAadhaar) {
                            if (input.length <= 12 && input.all { char -> char.isDigit() }) identityNumberInput = input
                        } else {
                            if (input.length <= 10) identityNumberInput = input.uppercase()
                        }
                    },
                    label = { Text(if (isAadhaar) "Aadhaar Card Number (12 Digits)" else "PAN Card Number (10 Characters)") },
                    placeholder = { Text(if (isAadhaar) "Enter 12-digit Aadhaar number" else "Enter 10-character PAN number") },
                    leadingIcon = { Icon(if (isAadhaar) Icons.Default.Badge else Icons.Default.CreditCard, contentDescription = null, tint = Slate400) },
                    keyboardOptions = KeyboardOptions(keyboardType = if (isAadhaar) KeyboardType.Number else KeyboardType.Text),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary)
                )

                // 4. Canvas CAPTCHA Component
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "SECURITY CAPTCHA VERIFICATION",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate400,
                        letterSpacing = 0.5.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Canvas Custom CAPTCHA Display
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .background(Slate100, RoundedCornerShape(12.dp))
                                .border(1.dp, Slate200, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            CaptchaCanvas(captchaText = captchaCode)
                        }

                        // Refresh CAPTCHA Button
                        IconButton(
                            onClick = { viewModel.refreshCaptcha() },
                            modifier = Modifier
                                .size(52.dp)
                                .background(PrimaryContainer, RoundedCornerShape(12.dp))
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh CAPTCHA", tint = Primary)
                        }
                    }

                    OutlinedTextField(
                        value = captchaInput,
                        onValueChange = { if (it.length <= 5) captchaInput = it.uppercase() },
                        label = { Text("Enter CAPTCHA Code") },
                        placeholder = { Text("5-character code above") },
                        leadingIcon = { Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Slate400) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary)
                    )
                }

                // Error Message Display
                AnimatedVisibility(visible = authState is AuthState.CaptchaFailed || authState is AuthState.Error) {
                    val errorMessage = when (authState) {
                        is AuthState.CaptchaFailed -> (authState as AuthState.CaptchaFailed).message
                        is AuthState.Error -> (authState as AuthState.Error).message
                        else -> ""
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Red50, RoundedCornerShape(10.dp))
                            .border(1.dp, Red100, RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Red700, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(errorMessage, fontSize = 12.sp, color = Red700, fontWeight = FontWeight.SemiBold)
                    }
                }

                // Action Button: Get OTP
                Button(
                    onClick = {
                        val typeStr = if (isAadhaar) "Aadhaar" else "PAN"
                        viewModel.requestOtp(phoneInput, typeStr, identityNumberInput, captchaInput)
                    },
                    enabled = isFormValid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Get OTP & Login", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Trust Badges
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Default.Shield, contentDescription = null, tint = Green700, modifier = Modifier.size(14.dp))
                Text("256-Bit Encrypted", fontSize = 10.sp, color = Slate500, fontWeight = FontWeight.SemiBold)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Primary, modifier = Modifier.size(14.dp))
                Text("DigiLocker Verified", fontSize = 10.sp, color = Slate500, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun CaptchaCanvas(captchaText: String) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Background noise lines
        drawLine(Color.LightGray, Offset(0f, height * 0.3f), Offset(width, height * 0.7f), strokeWidth = 2f)
        drawLine(Color.LightGray, Offset(0f, height * 0.8f), Offset(width, height * 0.2f), strokeWidth = 2f)
        drawLine(Color.Gray.copy(alpha = 0.3f), Offset(width * 0.2f, 0f), Offset(width * 0.8f, height), strokeWidth = 2f)

        // Random noise dots
        for (i in 0..20) {
            drawCircle(
                color = Color.DarkGray.copy(alpha = 0.2f),
                radius = 3f,
                center = Offset((0..width.toInt()).random().toFloat(), (0..height.toInt()).random().toFloat())
            )
        }

        // Draw randomized characters
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#005AC1")
            textSize = 42f
            isFakeBoldText = true
            typeface = android.graphics.Typeface.MONOSPACE
        }

        val charWidth = width / (captchaText.length + 1)
        captchaText.forEachIndexed { index, char ->
            val x = (index + 0.6f) * charWidth
            val y = height / 1.4f
            val rotationAngle = (-15..15).random().toFloat()

            rotate(rotationAngle, pivot = Offset(x, y)) {
                drawContext.canvas.nativeCanvas.drawText(char.toString(), x, y, paint)
            }
        }
    }
}
