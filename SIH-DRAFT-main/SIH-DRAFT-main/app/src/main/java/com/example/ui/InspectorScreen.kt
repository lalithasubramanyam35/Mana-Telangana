package com.example.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.*

@Composable
fun InspectorScreen(modifier: Modifier = Modifier, viewModel: InspectorViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val image1Uri by viewModel.image1Uri.collectAsState()
    val image2Uri by viewModel.image2Uri.collectAsState()
    val context = LocalContext.current

    val image1Launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        viewModel.setImage1(uri)
    }

    val image2Launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        viewModel.setImage2(uri)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "QA Inspector",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = OnPrimaryContainer
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            ImagePickerBox(
                label = "Complaint Photo",
                uri = image1Uri,
                onClick = { image1Launcher.launch("image/*") },
                modifier = Modifier.weight(1f)
            )
            ImagePickerBox(
                label = "Resolution Photo",
                uri = image2Uri,
                onClick = { image2Launcher.launch("image/*") },
                modifier = Modifier.weight(1f)
            )
        }

        Button(
            onClick = { viewModel.inspect(context) },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(16.dp),
            enabled = image1Uri != null && image2Uri != null && uiState !is InspectorUiState.Loading,
            colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = Color.White)
        ) {
            if (uiState is InspectorUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
            } else {
                Icon(Icons.Default.Analytics, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Run Quality Analysis", fontWeight = FontWeight.Bold)
            }
        }

        if (uiState is InspectorUiState.Error) {
            Text(
                text = "Error: ${(uiState as InspectorUiState.Error).message}",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        } else if (uiState is InspectorUiState.Success) {
            val analysis = (uiState as InspectorUiState.Success).analysis
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .border(1.dp, Slate100, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("VERIFICATION PASSED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Slate400, letterSpacing = 0.5.sp)
                    if (analysis.verification_passed) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Green600, modifier = Modifier.size(20.dp))
                    } else {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Red700, modifier = Modifier.size(20.dp))
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    val statusColor = if (analysis.verification_passed) Green600 else Red700
                    DataGridItem("Confidence", "${(analysis.confidence_score * 100).toInt()}%", modifier = Modifier.weight(1f))
                    DataGridItem("Action", analysis.recommended_action, modifier = Modifier.weight(1f), isMono = true)
                }

                if (analysis.fraud_flag_detected) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Red50, RoundedCornerShape(8.dp))
                            .border(1.dp, Red100, RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Red700, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("FRAUD FLAG DETECTED", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Red700)
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Slate50, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text("REASONING", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Slate400, letterSpacing = 0.5.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(analysis.reasoning, fontSize = 13.sp, color = Slate700, lineHeight = 20.sp)
                }
            }
        }
    }
}

@Composable
fun ImagePickerBox(label: String, uri: Uri?, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.aspectRatio(1f),
        border = BorderStroke(1.dp, Slate200),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (uri != null) Slate50 else Color.White
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = if (uri != null) Primary else Slate400, modifier = Modifier.size(32.dp))
            Spacer(Modifier.height(8.dp))
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = if (uri != null) Primary else Slate500)
            if (uri != null) {
                Text("Selected", fontSize = 10.sp, color = Green600, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun DataGridItem(label: String, value: String, modifier: Modifier = Modifier, isMono: Boolean = false) {
    Column(
        modifier = modifier
            .background(Slate50, RoundedCornerShape(8.dp))
            .border(1.dp, Slate100, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Text(label.uppercase(), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Slate400, letterSpacing = 0.5.sp)
        Spacer(Modifier.height(4.dp))
        Text(
            value.ifBlank { "N/A" }, 
            fontSize = if (isMono) 11.sp else 12.sp, 
            fontWeight = FontWeight.Bold, 
            color = if (isMono) Slate700 else Primary,
            fontFamily = if (isMono) FontFamily.Monospace else FontFamily.Default,
            maxLines = 1
        )
    }
}
