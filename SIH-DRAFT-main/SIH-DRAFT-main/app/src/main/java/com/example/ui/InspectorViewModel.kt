package com.example.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.repository.GrievanceRepository
import com.example.repository.ResolutionAnalysis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

sealed class InspectorUiState {
    object Idle : InspectorUiState()
    object Loading : InspectorUiState()
    data class Success(val analysis: ResolutionAnalysis) : InspectorUiState()
    data class Error(val message: String) : InspectorUiState()
}

class InspectorViewModel : ViewModel() {
    private val repository = GrievanceRepository()

    private val _uiState = MutableStateFlow<InspectorUiState>(InspectorUiState.Idle)
    val uiState: StateFlow<InspectorUiState> = _uiState.asStateFlow()

    private val _image1Uri = MutableStateFlow<Uri?>(null)
    val image1Uri: StateFlow<Uri?> = _image1Uri.asStateFlow()

    private val _image2Uri = MutableStateFlow<Uri?>(null)
    val image2Uri: StateFlow<Uri?> = _image2Uri.asStateFlow()

    fun setImage1(uri: Uri?) {
        _image1Uri.value = uri
    }

    fun setImage2(uri: Uri?) {
        _image2Uri.value = uri
    }

    fun inspect(context: Context) {
        val uri1 = _image1Uri.value ?: return
        val uri2 = _image2Uri.value ?: return
        
        _uiState.value = InspectorUiState.Loading
        
        viewModelScope.launch {
            try {
                val base64Img1 = uriToBase64(context, uri1)
                val base64Img2 = uriToBase64(context, uri2)
                
                val result = repository.inspectResolution(
                    image1Base64 = base64Img1,
                    image1MimeType = "image/jpeg",
                    image2Base64 = base64Img2,
                    image2MimeType = "image/jpeg"
                )
                _uiState.value = InspectorUiState.Success(result)
            } catch (e: Exception) {
                _uiState.value = InspectorUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    private fun uriToBase64(context: Context, uri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }
}
