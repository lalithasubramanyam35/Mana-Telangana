package com.example.ui.auth

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpVerificationBottomSheet(
    phone: String,
    simulatedOtp: String,
    viewModel: AuthViewModel,
    onDismiss: () -> Unit
) {
    var otpValues by remember { mutableStateOf(List(6) { "" }) }
    var isSmsAutoFilled by remember { mutableStateOf(false) }
    val focusRequesters = remember { List(6) { FocusRequester() } }

    val countdownSeconds by viewModel.otpCountdownSeconds.collectAsState()
    val authState by viewModel.authState.collectAsState()

    // SIMULATED REALTIME SMS RETRIEVER ENGINE: AUTO-FILL OTP FROM INCOMING MESSAGE
    LaunchedEffect(simulatedOtp) {
        delay(600) // Simulate 600ms SMS delivery & Android SMSRetriever API detection
        otpValues = simulatedOtp.map { it.toString() }
        isSmsAutoFilled = true
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(PrimaryContainer, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Sms, contentDescription = null, tint = Primary, modifier = Modifier.size(24.dp))
            }

            Text("Enter Verification Code", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = OnPrimaryContainer)
            Text("We have sent a 6-digit SMS OTP to +91 $phone", fontSize = 12.sp, color = Slate500, textAlign = TextAlign.Center)

            // Realtime SMS Detection & Auto-Fill Status Banner
            AnimatedVisibility(visible = isSmsAutoFilled) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Green50, RoundedCornerShape(10.dp))
                        .border(1.dp, Green100, RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.MarkEmailRead, contentDescription = null, tint = Green700, modifier = Modifier.size(18.dp))
                        Column {
                            Text("SMS RETRIEVER: AUTO-FILLED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Green700)
                            Text("Received OTP Code: $simulatedOtp", fontSize = 11.sp, color = Slate700, fontFamily = FontFamily.Monospace)
                        }
                    }
                    Button(
                        onClick = {
                            otpValues = simulatedOtp.map { it.toString() }
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Green700),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("Auto-Fill", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // 6-Digit OTP Boxes
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                for (i in 0 until 6) {
                    OutlinedTextField(
                        value = otpValues[i],
                        onValueChange = { newValue ->
                            if (newValue.length <= 1 && newValue.all { it.isDigit() }) {
                                val updated = otpValues.toMutableList()
                                updated[i] = newValue
                                otpValues = updated

                                // Auto focus move to next
                                if (newValue.isNotEmpty() && i < 5) {
                                    focusRequesters[i + 1].requestFocus()
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .focusRequester(focusRequesters[i]),
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = Primary
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary)
                    )
                }
            }

            // Resend Timer Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (countdownSeconds > 0) {
                    Text("Resend code in ${countdownSeconds}s", fontSize = 12.sp, color = Slate400, fontWeight = FontWeight.Medium)
                } else {
                    TextButton(onClick = { viewModel.resendOtp(phone) }) {
                        Icon(Icons.Default.LockReset, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Resend OTP", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Action Button: Verify OTP
            val isOtpComplete = otpValues.all { it.isNotEmpty() }
            val fullOtp = otpValues.joinToString("")

            Button(
                onClick = { viewModel.verifyOtp(phone, fullOtp) },
                enabled = isOtpComplete && authState !is AuthState.Authenticating,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                if (authState is AuthState.Authenticating) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Verify & Enter CivicPulse", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
