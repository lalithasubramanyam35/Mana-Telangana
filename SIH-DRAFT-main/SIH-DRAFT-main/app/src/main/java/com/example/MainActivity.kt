package com.example

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.ManageSearch
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.GrievanceIntakeScreen
import com.example.ui.HeatmapScreen
import com.example.ui.InspectorScreen
import com.example.ui.LandHealthScreen
import com.example.ui.PensionMeeSevaScreen
import com.example.ui.auth.AuthState
import com.example.ui.auth.AuthViewModel
import com.example.ui.auth.LoginScreen
import com.example.ui.auth.OtpVerificationBottomSheet
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
  @OptIn(ExperimentalMaterial3Api::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Enforce 120Hz High Refresh Rate mode for buttery smooth scrolling & animations
    val layoutParams = window.attributes
    layoutParams.preferredRefreshRate = 120f
    window.attributes = layoutParams
    @Suppress("DEPRECATION")
    window.addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)

    setContent {
      val authViewModel: AuthViewModel = viewModel()
      val authState by authViewModel.authState.collectAsState()

      var selectedTab by remember { mutableIntStateOf(0) }
      var showInspectorDialog by remember { mutableStateOf(false) }

      MyApplicationTheme {
        if (authState is AuthState.Authenticated) {
          // AUTHENTICATED STATE: MAIN 4-TAB DASHBOARD
          val citizenName = (authState as AuthState.Authenticated).citizenName

          Scaffold(
            topBar = {
              CivicPulseHeader(
                citizenName = citizenName,
                onOpenInspector = { showInspectorDialog = true },
                onLogout = { authViewModel.logout() }
              )
            },
            bottomBar = { CivicPulseBottomNav(selectedTab = selectedTab, onTabSelected = { selectedTab = it }) },
            modifier = Modifier.fillMaxSize()
          ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
              AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                  (fadeIn(animationSpec = tween(220, easing = LinearOutSlowInEasing)) + slideInHorizontally(animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessLow))) togetherWith
                  (fadeOut(animationSpec = tween(180, easing = FastOutLinearInEasing)) + slideOutHorizontally(animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessLow)))
                },
                label = "120HzTabTransition"
              ) { targetTab ->
                when (targetTab) {
                  0 -> GrievanceIntakeScreen()
                  1 -> PensionMeeSevaScreen()
                  2 -> LandHealthScreen()
                  3 -> HeatmapScreen(onOpenInspector = { showInspectorDialog = true })
                }
              }
            }

            if (showInspectorDialog) {
              ModalBottomSheet(
                onDismissRequest = { showInspectorDialog = false },
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                containerColor = Color.White
              ) {
                Box(modifier = Modifier.fillMaxHeight(0.9f).padding(8.dp)) {
                  InspectorScreen()
                }
              }
            }
          }
        } else {
          // LOGGED OUT / AUTHENTICATING STATE: LOGIN GATEWAY
          Box(modifier = Modifier.fillMaxSize()) {
            LoginScreen(viewModel = authViewModel)

            if (authState is AuthState.OtpSent) {
              val otpState = authState as AuthState.OtpSent
              OtpVerificationBottomSheet(
                phone = otpState.phone,
                simulatedOtp = otpState.simulatedOtp,
                viewModel = authViewModel,
                onDismiss = { authViewModel.logout() }
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun CivicPulseHeader(
  citizenName: String = "K. Srinivas",
  onOpenInspector: () -> Unit = {},
  onLogout: () -> Unit = {}
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .background(Color.White)
      .padding(start = 16.dp, end = 16.dp, top = 44.dp, bottom = 8.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Box(
          modifier = Modifier
            .size(40.dp)
            .background(
              brush = Brush.linearGradient(listOf(Primary, CyberBlue)),
              shape = RoundedCornerShape(12.dp)
            ),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.Analytics, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(citizenName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = OnPrimaryContainer, letterSpacing = (-0.3).sp, maxLines = 1)
            Box(
              modifier = Modifier
                .background(GoldLight, RoundedCornerShape(6.dp))
                .border(1.dp, GoldBorder, RoundedCornerShape(6.dp))
                .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
              Text("Verified", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = TelanganaGold, maxLines = 1)
            }
          }
          Text("CivicPulse Node • Telangana SSO", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Slate500, maxLines = 1)
        }
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        // QA Inspector Trigger
        IconButton(
          onClick = onOpenInspector,
          modifier = Modifier
            .size(34.dp)
            .background(Slate100, CircleShape)
        ) {
          Icon(Icons.AutoMirrored.Filled.ManageSearch, contentDescription = "QA Inspector", tint = Primary, modifier = Modifier.size(18.dp))
        }

        // Logout Trigger
        IconButton(
          onClick = onLogout,
          modifier = Modifier
            .size(34.dp)
            .background(Red50, CircleShape)
        ) {
          Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout", tint = Red700, modifier = Modifier.size(18.dp))
        }
      }
    }

    // Live Statistics Ticker Bar
    Spacer(Modifier.height(8.dp))
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(Slate50, RoundedCornerShape(10.dp))
        .border(1.dp, Slate100, RoundedCornerShape(10.dp))
        .padding(horizontal = 6.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      StatTickerItem(label = "PROCESSED", value = "14,280", modifier = Modifier.weight(1f))
      Box(modifier = Modifier.height(18.dp).width(1.dp).background(Slate200))
      StatTickerItem(label = "SLA RATE", value = "98.4%", isGreen = true, modifier = Modifier.weight(1f))
      Box(modifier = Modifier.height(18.dp).width(1.dp).background(Slate200))
      StatTickerItem(label = "AI ACCURACY", value = "99.1%", isBlue = true, modifier = Modifier.weight(1f))
    }

    HorizontalDivider(modifier = Modifier.padding(top = 8.dp), color = Slate200)
  }
}

@Composable
fun StatTickerItem(label: String, value: String, isGreen: Boolean = false, isBlue: Boolean = false, modifier: Modifier = Modifier) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
    modifier = modifier
  ) {
    Text(label, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Slate400, maxLines = 1)
    Text(
      value,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      fontFamily = FontFamily.Monospace,
      color = if (isGreen) Green700 else if (isBlue) Primary else Slate800,
      maxLines = 1
    )
  }
}

@Composable
fun CivicPulseBottomNav(selectedTab: Int, onTabSelected: (Int) -> Unit) {
  NavigationBar(
    containerColor = Color.White,
    tonalElevation = 8.dp,
    modifier = Modifier.fillMaxWidth()
  ) {
    NavigationBarItem(
      icon = {
        BadgedBox(
          badge = {
            Badge(containerColor = Primary) { Text("AI", fontSize = 8.sp, color = Color.White) }
          }
        ) {
          Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = "Grievance Intake")
        }
      },
      label = { Text("Grievance", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
      selected = selectedTab == 0,
      onClick = { onTabSelected(0) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = Primary,
        selectedTextColor = Primary,
        indicatorColor = PrimaryContainer,
        unselectedIconColor = Slate500,
        unselectedTextColor = Slate500
      )
    )

    NavigationBarItem(
      icon = { Icon(Icons.Default.AccountBalance, contentDescription = "Pension & MeeSeva") },
      label = { Text("Pension/MeeSeva", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
      selected = selectedTab == 1,
      onClick = { onTabSelected(1) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = Primary,
        selectedTextColor = Primary,
        indicatorColor = PrimaryContainer,
        unselectedIconColor = Slate500,
        unselectedTextColor = Slate500
      )
    )

    NavigationBarItem(
      icon = { Icon(Icons.Default.Landscape, contentDescription = "Land & Health") },
      label = { Text("Land & Health", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
      selected = selectedTab == 2,
      onClick = { onTabSelected(2) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = Primary,
        selectedTextColor = Primary,
        indicatorColor = PrimaryContainer,
        unselectedIconColor = Slate500,
        unselectedTextColor = Slate500
      )
    )

    NavigationBarItem(
      icon = {
        BadgedBox(
          badge = {
            Badge(containerColor = Red600) { Text("4", fontSize = 8.sp, color = Color.White) }
          }
        ) {
          Icon(Icons.Default.Map, contentDescription = "Public Heatmap")
        }
      },
      label = { Text("Heatmap", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
      selected = selectedTab == 3,
      onClick = { onTabSelected(3) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = Primary,
        selectedTextColor = Primary,
        indicatorColor = PrimaryContainer,
        unselectedIconColor = Slate500,
        unselectedTextColor = Slate500
      )
    )
  }
}
