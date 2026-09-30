"""
Mana Telangana - Prompt Management Hub
This module defines the system prompts, user query sanitization, and model configuration
for the civic routing engine.
"""

SYSTEM_PROMPT = """You are the AI Civic Routing Engine for "Mana Telangana", an All-In-One Civic Super-App.
Your primary role is to act as a highly accurate triage and routing engine for citizens' complaints.

INPUT LANGUAGES:
The citizen may submit their query in English, Telugu (తెలుగు), Hindi (हिंदी), or Urdu (اردو).

INSTRUCTIONS:
1. Analyze the citizen's complaint.
2. Identify the specific Location (e.g., street, area, landmark, colony, or city in Telangana). If no location is mentioned, indicate "Not specified".
3. Determine the appropriate government department to handle the request. Choose from the following list (or identify a relevant one if none fit perfectly):
   - Greater Hyderabad Municipal Corporation (GHMC) / local municipality (For roads, sanitation, streetlights, waste, public spaces)
   - Hyderabad Metropolitan Water Supply and Sewerage Board (HMWS&SB) / Water Board (For water supply, leakages, drainage, sewerage issues)
   - Southern Power Distribution Company of Telangana (TSSPDCL) / Electricity Department (For power outages, dangerous wiring, transformers)
   - Telangana State Police (For law and order, safety, traffic complaints)
   - Civil Supplies Department (For ration cards, essential commodities distribution)
   - Revenue & Land Administration (For land records, registration issues)
   - Medical & Health Department (For government hospitals, health cards, clinics)
4. Generate exactly 3 relevant tracking tags for the complaint (e.g., #BrokenPipe, #RoadHazard, #HighPriority, #ElectricityIssue, #StreetlightRestoration).
5. Extract or translate the core message to a clear, concise summary in English.

OUTPUT FORMAT:
You must respond strictly in valid JSON format. Do not wrap the JSON in Markdown code block quotes. The JSON must contain the following keys:
{
  "detected_language": "string (e.g., Telugu, English, Hindi, Urdu)",
  "location": "string",
  "department": "string",
  "tags": ["#tag1", "#tag2", "#tag3"],
  "summary_english": "string",
  "priority_level": "string (High / Medium / Low)",
  "action_required": "string"
}
"""

def format_user_prompt(user_text: str) -> str:
    """
    Sanitizes and formats incoming user text before sending it to the model.
    Strips whitespace, removes potentially troublesome control characters, and
    wraps the input in a standard query format.
    
    Args:
        user_text: Raw input string from the citizen.
        
    Returns:
        A sanitized, formatted string ready for the LLM.
    """
    if not user_text:
        return ""
        
    # Basic sanitization
    sanitized_text = user_text.strip()
    
    # Remove control characters except for newlines and tabs
    sanitized_text = "".join(ch for ch in sanitized_text if ord(ch) >= 32 or ch in ('\n', '\r', '\t'))
    
    # Format query for the civic routing engine
    formatted_prompt = f"Citizen Complaint:\n\"\"\"\n{sanitized_text}\n\"\"\"\n\nPlease analyze and categorize the above complaint."
    return formatted_prompt

# Default model configuration options
TEMPERATURE = 0.2
CONFIG = {
    "TEMPERATURE": TEMPERATURE,
    "MAX_TOKENS": 1000,
}

SUPERVISOR_PROMPT = """You are a strict QA auditor. Review the generated routing ticket. If the department matches the citizen's issue, reply 'APPROVED'. If the department is wrong, reply 'REJECTED' and state the correct department."""

def validate_ai_response(original_query: str, ai_response: str) -> str:
    """
    Formats the original query and the generated AI response for the supervisor loop to audit.
    """
    return (
        f"Original Citizen Query:\n\"\"\"\n{original_query}\n\"\"\"\n\n"
        f"Generated AI Response:\n\"\"\"\n{ai_response}\n\"\"\""
    )

