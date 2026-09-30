package com.example.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

sealed class AuthState {
    object LoggedOut : AuthState()
    data class CaptchaFailed(val message: String) : AuthState()
    data class OtpSent(val phone: String, val simulatedOtp: String) : AuthState()
    object Authenticating : AuthState()
    data class Authenticated(val citizenName: String, val sessionToken: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.LoggedOut)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _captchaCode = MutableStateFlow("")
    val captchaCode: StateFlow<String> = _captchaCode.asStateFlow()

    private val _otpCountdownSeconds = MutableStateFlow(30)
    val otpCountdownSeconds: StateFlow<Int> = _otpCountdownSeconds.asStateFlow()

    init {
        refreshCaptcha()
    }

    fun refreshCaptcha() {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        val code = (1..5).map { chars[Random.nextInt(chars.length)] }.joinToString("")
        _captchaCode.value = code
    }

    fun requestOtp(phone: String, identityType: String, identityNumber: String, userCaptchaInput: String) {
        if (phone.length < 10) {
            _authState.value = AuthState.Error("Please enter a valid 10-digit mobile number.")
            return
        }

        if (identityType == "Aadhaar" && identityNumber.length < 12) {
            _authState.value = AuthState.Error("Please enter a valid 12-digit Aadhaar number.")
            return
        }

        if (identityType == "PAN" && identityNumber.length < 10) {
            _authState.value = AuthState.Error("Please enter a valid 10-character PAN number.")
            return
        }

        if (!userCaptchaInput.equals(_captchaCode.value, ignoreCase = true)) {
            _authState.value = AuthState.CaptchaFailed("Incorrect CAPTCHA. Please try again.")
            refreshCaptcha()
            return
        }

        // Simulate OTP Generation (e.g. 482910)
        val generatedOtp = "482910"
        _authState.value = AuthState.OtpSent(phone, generatedOtp)
        startOtpCountdown()
    }

    fun verifyOtp(phone: String, userOtpInput: String) {
        if (userOtpInput.length < 6) {
            _authState.value = AuthState.Error("Please enter complete 6-digit OTP.")
            return
        }

        _authState.value = AuthState.Authenticating
        viewModelScope.launch {
            delay(1000) // Simulate network eKYC verification delay
            if (userOtpInput == "482910" || userOtpInput == "123456") {
                _authState.value = AuthState.Authenticated(
                    citizenName = "K. Srinivas",
                    sessionToken = "TS-EKYC-TOKEN-994819"
                )
            } else {
                _authState.value = AuthState.Error("Invalid OTP. Enter 482910 for demo.")
            }
        }
    }

    private fun startOtpCountdown() {
        _otpCountdownSeconds.value = 30
        viewModelScope.launch {
            while (_otpCountdownSeconds.value > 0) {
                delay(1000)
                _otpCountdownSeconds.value -= 1
            }
        }
    }

    fun resendOtp(phone: String) {
        val generatedOtp = "482910"
        _authState.value = AuthState.OtpSent(phone, generatedOtp)
        startOtpCountdown()
    }

    fun logout() {
        _authState.value = AuthState.LoggedOut
        refreshCaptcha()
    }
}
