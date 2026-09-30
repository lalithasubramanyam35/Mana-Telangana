package com.example.repository

import com.example.BuildConfig
import com.example.network.Content
import com.example.network.GenerateContentRequest
import com.example.network.GenerationConfig
import com.example.network.Part
import com.example.network.ResponseFormat
import com.example.network.ResponseFormatText
import com.example.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.add
import kotlinx.serialization.json.JsonPrimitive

@Serializable
data class GrievanceAnalysis(
    val language_detected: String = "",
    val raw_summary: String = "",
    val category: String = "",
    val urgency_score: String = "",
    val confidence_score: Double = 0.0,
    val actionable_location_text: String = "",
    val target_department_code: String = "",
    val suggested_sla_hours: Int = 0
)

@Serializable
data class ResolutionAnalysis(
    val verification_passed: Boolean = false,
    val confidence_score: Double = 0.0,
    val reasoning: String = "",
    val fraud_flag_detected: Boolean = false,
    val recommended_action: String = ""
)

class GrievanceRepository {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun analyzeGrievance(text: String): GrievanceAnalysis = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            throw Exception("API key is missing or default. Configure it in Secrets.")
        }

        val prompt = "Analyze the following grievance text according to the system instructions:\n$text"

        val schema = buildJsonObject {
            put("type", "OBJECT")
            putJsonObject("properties") {
                putJsonObject("language_detected") { put("type", "STRING") }
                putJsonObject("raw_summary") { put("type", "STRING") }
                putJsonObject("category") { put("type", "STRING") }
                putJsonObject("urgency_score") { put("type", "STRING") }
                putJsonObject("confidence_score") { put("type", "NUMBER") }
                putJsonObject("actionable_location_text") { put("type", "STRING") }
                putJsonObject("target_department_code") { put("type", "STRING") }
                putJsonObject("suggested_sla_hours") { put("type", "INTEGER") }
            }
            putJsonArray("required") {
                add("language_detected")
                add("raw_summary")
                add("category")
                add("urgency_score")
                add("confidence_score")
                add("actionable_location_text")
                add("target_department_code")
                add("suggested_sla_hours")
            }
        }

        val systemPrompt = """
            You are the core AI Engine for CivicPulse, an enterprise Indian civic grievance platform.
            Your task is to process unstructured voice transcriptions or raw text submitted by citizens in English, Hindi, Telugu, Tamil, or regional slang.
            
            Process the input and return ONLY a valid JSON object matching the requested schema.
            
            Category must be one of: Sanitation | Roads & Infrastructure | Electrical & Power | Water Supply | Education | Healthcare | Public Corruption
            Urgency score must be one of: Low | Medium | High | Critical (Critical reserved for live hazards like exposed wires, broken water mains)
            
            Rule: If the audio/text contains allegations of bribery or corruption, set category to "Public Corruption", target_department_code to "STATE_VIGILANCE_COMMISSION", and trigger whistleblower mode flags.
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            systemInstruction = Content(parts = listOf(Part(text = systemPrompt))),
            generationConfig = GenerationConfig(
                temperature = 0.1f,
                responseFormat = ResponseFormat(
                    text = ResponseFormatText(
                        mimeType = "application/json",
                        schema = schema
                    )
                )
            )
        )

        val response = RetrofitClient.service.generateContent(apiKey, request)
        val responseText = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: throw Exception("Empty response from API")

        json.decodeFromString<GrievanceAnalysis>(responseText)
    }

    suspend fun inspectResolution(image1Base64: String, image1MimeType: String, image2Base64: String, image2MimeType: String): ResolutionAnalysis = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            throw Exception("API key is missing or default. Configure it in Secrets.")
        }

        val prompt = "Analyze these two images. Image 1 is the Original Citizen Complaint Photo. Image 2 is the Ground Officer's Claimed Resolution Photo. Evaluate if the reported issue has been genuinely resolved."

        val schema = buildJsonObject {
            put("type", "OBJECT")
            putJsonObject("properties") {
                putJsonObject("verification_passed") { put("type", "BOOLEAN") }
                putJsonObject("confidence_score") { put("type", "NUMBER") }
                putJsonObject("reasoning") { put("type", "STRING") }
                putJsonObject("fraud_flag_detected") { put("type", "BOOLEAN") }
                putJsonObject("recommended_action") { put("type", "STRING") }
            }
            putJsonArray("required") {
                add("verification_passed")
                add("confidence_score")
                add("reasoning")
                add("fraud_flag_detected")
                add("recommended_action")
            }
        }

        val systemPrompt = """
            You are an automated Municipal Quality Assurance AI Inspector.
            You will receive two images: 
            Image 1: Original Citizen Complaint Photo.
            Image 2: Ground Officer's Claimed Resolution Photo.

            Analyze both images and evaluate if the reported issue (e.g., pothole, garbage dump, broken streetlight) has been genuinely resolved.

            Return JSON strictly. Recommended action must be one of: Close_Ticket | Reject_Resolution_Escalate | Require_Manual_Human_Audit
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(
                Content(parts = listOf(
                    Part(text = prompt),
                    Part(inlineData = com.example.network.InlineData(mimeType = image1MimeType, data = image1Base64)),
                    Part(inlineData = com.example.network.InlineData(mimeType = image2MimeType, data = image2Base64))
                ))
            ),
            systemInstruction = Content(parts = listOf(Part(text = systemPrompt))),
            generationConfig = GenerationConfig(
                temperature = 0.1f,
                responseFormat = ResponseFormat(
                    text = ResponseFormatText(
                        mimeType = "application/json",
                        schema = schema
                    )
                )
            )
        )

        val response = RetrofitClient.service.generateContent(apiKey, request)
        val responseText = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: throw Exception("Empty response from API")

        json.decodeFromString<ResolutionAnalysis>(responseText)
    }
}
