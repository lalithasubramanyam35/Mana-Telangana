package com.example.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.repository.GrievanceAnalysis
import com.example.repository.GrievanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class GrievanceUiState {
    object Idle : GrievanceUiState()
    object Loading : GrievanceUiState()
    data class Success(val analysis: GrievanceAnalysis) : GrievanceUiState()
    data class Error(val message: String) : GrievanceUiState()
}

enum class VerificationTier(val displayName: String) {
    MOBILE_OTP("Mobile OTP"),
    DIGILOCKER_EKYC("DigiLocker eKYC / PAN"),
    WHISTLEBLOWER("Zero-Knowledge Whistleblower Mode")
}

class GrievanceViewModel : ViewModel() {
    private val repository = GrievanceRepository()

    private val _uiState = MutableStateFlow<GrievanceUiState>(GrievanceUiState.Idle)
    val uiState: StateFlow<GrievanceUiState> = _uiState.asStateFlow()

    private val _selectedVerificationTier = MutableStateFlow(VerificationTier.MOBILE_OTP)
    val selectedVerificationTier: StateFlow<VerificationTier> = _selectedVerificationTier.asStateFlow()

    private val _photoEvidenceUri = MutableStateFlow<Uri?>(null)
    val photoEvidenceUri: StateFlow<Uri?> = _photoEvidenceUri.asStateFlow()

    private val _gpsLocationTag = MutableStateFlow("Hyderabad, Telangana • 17.3850° N, 78.4867° E")
    val gpsLocationTag: StateFlow<String> = _gpsLocationTag.asStateFlow()

    fun setVerificationTier(tier: VerificationTier) {
        _selectedVerificationTier.value = tier
    }

    fun setPhotoEvidence(uri: Uri?) {
        _photoEvidenceUri.value = uri
    }

    fun setGpsLocation(locationStr: String) {
        _gpsLocationTag.value = locationStr
    }

    fun analyze(text: String) {
        if (text.isBlank()) return
        
        _uiState.value = GrievanceUiState.Loading
        viewModelScope.launch {
            try {
                val processedInput = if (_selectedVerificationTier.value == VerificationTier.WHISTLEBLOWER) {
                    "[WHISTLEBLOWER_ANONYMOUS_ROUTING] $text"
                } else {
                    text
                }
                val result = repository.analyzeGrievance(processedInput)
                _uiState.value = GrievanceUiState.Success(result)
            } catch (e: Exception) {
                _uiState.value = GrievanceUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
