import sys
import importlib
import streamlit as st
import re
import hashlib
import time

# Force reloading to clear Streamlit caching of imported local modules
if "translations" in sys.modules:
    importlib.reload(sys.modules["translations"])
if "backend" in sys.modules:
    importlib.reload(sys.modules["backend"])

from translations import TRANSLATIONS

# Set page config
st.set_page_config(
    page_title="Prajavani AI Civic Super-App",
    page_icon="🏛️",
    layout="wide",
    initial_sidebar_state="expanded"
)

# 4. SEAMLESS INTEGRATION (Backend Imports)
try:
    from backend import (
        process_civic_complaint,
        supervisor_validation_check,
        generate_legal_affidavit,
        reconcile_land_documents,
        verify_visual_audit,
        generate_pensioner_payslip,
        generate_zkp_token,
        verify_audio_evidence,
        format_file_size,
        is_query_trivial,
        transcribe_audio_simulation,
        generate_official_receipt_pdf,
        check_geospatial_duplicate,
        generate_rti_draft_pdf,
        verify_ecourts_subjudice,
        audit_officer_kpi_anomalies,
        audit_acb_complaint_evidence
    )
except ImportError:
    # Safe fallbacks if backend file is unavailable
    def format_file_size(size_bytes: int):
        return f"{size_bytes / 1024:.2f} KB"
    def is_query_trivial(text: str):
        return len(text.strip().split()) < 3
    def transcribe_audio_simulation(audio_bytes: bytes):
        return "Water pipe leakage and water logging on the road near Ameerpet Metro Station."
    def generate_official_receipt_pdf(ticket_data: dict):
        return b"Mock PDF Bytes"
    def verify_audio_evidence(audio_bytes: bytes):
        return {
            "format": "WAV",
            "sample_rate": "16000 Hz",
            "channels": "Mono",
            "duration": "Calculated",
            "size_bytes": len(audio_bytes),
            "size_formatted": format_file_size(len(audio_bytes)),
            "checksum": f"TS-AUDIO-HASH-{hashlib.sha256(audio_bytes).hexdigest()[:8].upper()}"
        }
    def check_geospatial_duplicate(location_text, category):
        return {"duplicate_found": False, "master_id": None, "citizen_count": 0, "landmark": None}
    def generate_rti_draft_pdf(ticket_data):
        return b"Mock RTI PDF Bytes"
    def process_civic_complaint(user_text, language="English", audio_bytes=None):
        return {
            "detected_language": "English",
            "location": "Ameerpet Metro Zone, Hyderabad",
            "initial_department": "Greater Hyderabad Municipal Corporation (GHMC)",
            "final_department": "Hyderabad Metropolitan Water Supply and Sewerage Board (HMWS&SB)",
            "corrected": True,
            "audit_trail": "FALLBACK: Rerouted from GHMC to HMWS&SB.",
            "severity": "High",
            "tags": ["#WaterLeakage", "#BrokenPipe"],
            "summary": user_text[:100] if user_text else "Audio Grievance",
            "tracking_id": f"TS-PRJ-2026-{hashlib.sha256(user_text.encode() if user_text else b'audio').hexdigest()[:5].upper()}",
            "pdf_bytes": b"Mock PDF Bytes"
        }
    def supervisor_validation_check(original_query, assigned_dept):
        return {"corrected": False, "initial_department": assigned_dept, "final_department": assigned_dept, "audit_trail": "Approved"}
    def generate_legal_affidavit(citizen_name, wrong_name_on_id, document_type):
        return f"BEFORE THE NOTARY PUBLIC\n\nFALLBACK AFFIDAVIT FOR {citizen_name}", b"Mock PDF Bytes", "TS-AFF-2026-MOCK"
    def reconcile_land_documents(doc_text, survey_number):
        return {"status": "Reconciliation Successful", "code": "CLEAR_TITLE", "message": "Clear", "details": "Success", "pdf_bytes": b"Mock PDF", "tracking_no": "TS-LND-MOCK"}
    def verify_visual_audit(before, after):
        return {"status": "AUDIT_APPROVED", "score": 90.0, "message": "Visual match success.", "details": "Cleared."}
    def generate_pensioner_payslip(treasury_id, phone_num):
        return b"Mock Payslip PDF Bytes", "TS-PAY-2026-MOCK"
    def generate_zkp_token(acb_text):
        return "ACB-ZKP-2026-MOCK"
    def verify_ecourts_subjudice(ticket_id, case_number):
        return {"subjudice": False, "case_title": None, "court": None, "status": "NOT_FOUND", "message": "Fallback active."}
    def audit_officer_kpi_anomalies(officer_id, resolved_tickets):
        return {"anomaly_found": False, "status": "Clean", "anomaly_score": 0, "flagged": False, "message": "Fallback active.", "severity": "NORMAL", "audit_logs": []}
    def audit_acb_complaint_evidence(officer_name, dept, bribe_amount, time_window, evidence_files, cctv_requested):
        return {"status": "INSUFFICIENT_PROOF", "action": "CASE_SUSPENDED", "stage": "Stage 1: Evidence Verification", "message": "Notice"}

# Initialize Session Ticket Database Ledger
if 'grievance_db' not in st.session_state:
    st.session_state['grievance_db'] = []
if 'active_ticket' not in st.session_state:
    st.session_state['active_ticket'] = None

# --- 1. DUAL-ENGINE RESPONSIVE ARCHITECTURE (WEB & MOBILE APP PWA) ---
mobile_pwa_head = """
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
        <meta name="theme-color" content="#002B49">
        <meta name="apple-mobile-web-app-capable" content="yes">
        <meta name="apple-mobile-web-app-status-bar-style" content="black-translucent">
        <meta name="apple-mobile-web-app-title" content="Mana Telangana">
        <link rel="manifest" href="data:application/json;base64,eyJuYW1lIjoiTWFuYSBUZWxhbmdhbmEgQXBwIiwic2hvcnJfbmFtZSI6Ik1hbmFUZWxhbmdhbmEiLCJzdGFydF91cmwiOiIvIiwiZGlzcGxheSI6InN0YW5kYWxvbmUiLCJiYWNrZ3JvdW5kX2NvbG9yIjoiI2YwZjJmNSIsInRoZW1lX2NvbG9yIjoiIzAwMmI0OSJ9">
    </head>
"""
st.markdown(mobile_pwa_head, unsafe_allow_html=True)

# --- 2. CLEAN GOVERNMENT PORTAL DESIGN & THEMING (CSS OVERRIDES) ---
hide_streamlit_style = """
    <style>
    #MainMenu {visibility: hidden;}
    footer {visibility: hidden;}
    header {visibility: hidden;}
    .stDeployButton {display:none;}
    </style>
"""
st.markdown(hide_streamlit_style, unsafe_allow_html=True)

# --- 2. OFFICIAL TELANGANA PORTAL HEADER ARCHITECTURE ---
# Top Utility Strip (Solid Dark Navy)
st.markdown(
    """
    <div class="top-utility-bar" style="background-color: #002b49; color: #ffffff; padding: 6px 20px; font-size: 0.8rem; font-weight: 600; display: flex; justify-content: space-between; align-items: center; border-radius: 6px 6px 0 0; border-bottom: 2px solid #FF9933;">
        <div>GOVERNMENT OF TELANGANA | CGG PORTAL SERVICES</div>
        <div style="letter-spacing: 0.5px;">OFFICIAL STATE CITIZEN GATEWAY</div>
    </div>
    """, unsafe_allow_html=True
)

col_logo, col_f_resizer, col_lang_sel, col_toggle = st.columns([5.5, 1.8, 1.8, 1.9])

with col_lang_sel:
    language = st.selectbox("Language Selector", ["English", "తెలుగు", "Urdu", "हिंदी"], index=0, key="lang_select", label_visibility="collapsed")

class SafeTranslationDict:
    def __init__(self, target_dict, fallback_dict):
        self.target_dict = target_dict
        self.fallback_dict = fallback_dict
        
    def __getitem__(self, key):
        if key in self.target_dict:
            return self.target_dict[key]
        if key in self.fallback_dict:
            return self.fallback_dict[key]
        return key
        
    def get(self, key, default=None):
        if key in self.target_dict:
            return self.target_dict[key]
        if key in self.fallback_dict:
            return self.fallback_dict[key]
        return default if default is not None else key

# Bind localized text map
t = SafeTranslationDict(TRANSLATIONS.get(language, {}), TRANSLATIONS.get("English", {}))

with col_logo:
    st.markdown(
        f"""
        <div style="display: flex; align-items: center; gap: 15px; margin-top: 5px;">
            <!-- Emblem SVG Graphic (Kakatiya Arch & Charminar Seal) -->
            <svg width="65" height="65" viewBox="0 0 100 100" style="flex-shrink: 0;">
                <circle cx="50" cy="50" r="46" fill="none" stroke="#138808" stroke-width="3.5" />
                <circle cx="50" cy="50" r="41" fill="none" stroke="#FF9933" stroke-width="1.5" />
                <path d="M 30 75 L 30 50 A 20 20 0 0 1 70 50 L 70 75" fill="none" stroke="#003366" stroke-width="4.5" stroke-linecap="round" />
                <path d="M 23 75 L 77 75" stroke="#003366" stroke-width="4.5" stroke-linecap="round" />
                <path d="M 30 44 L 70 44" stroke="#003366" stroke-width="2" />
                <rect x="42" y="52" width="16" height="23" fill="none" stroke="#138808" stroke-width="2" />
                <circle cx="45" cy="52" r="1.5" fill="#138808" />
                <circle cx="55" cy="52" r="1.5" fill="#138808" />
                <line x1="42" y1="62" x2="58" y2="62" stroke="#138808" stroke-width="1.5" />
                <line x1="50" y1="52" x2="50" y2="75" stroke="#138808" stroke-width="1.2" />
                <rect x="47" y="24" width="6" height="12" fill="#FF9933" />
                <circle cx="50" cy="22" r="3.5" fill="#FF9933" />
                <path d="M 45 36 L 55 36" stroke="#FF9933" stroke-width="2.5" />
            </svg>
            <div>
                <strong style="color: var(--logo-title-color, #003366); font-size: 1.1rem; letter-spacing: 0.5px; display: block; margin: 0; line-height: 1.3;">
                    {t['title']} | తెలంగాణ ప్రభుత్వము | تلنگانہ حکومت
                </strong>
                <span style="font-size: 0.8rem; color: var(--text-color); opacity: 0.85; font-weight: 600; display: block; margin-top: 3px;">
                    {t['subtitle']}
                </span>
            </div>
        </div>
        """, unsafe_allow_html=True
    )

with col_f_resizer:
    font_size = st.radio("Font Size", ["A-", "A", "A+"], horizontal=True, index=1, key="font_size_radio", label_visibility="collapsed")

with col_toggle:
    dark_mode = st.toggle("🌙 Dark Mode", value=False, key="theme_toggle")

# Font Size mapping
font_size_px = "16px"
if font_size == "A-":
    font_size_px = "14px"
elif font_size == "A+":
    font_size_px = "18px"

# Adaptive Theme variables setup (GIGW Compliant Colors)
if dark_mode:
    theme_variables = f"""
    :root {{
        font-size: {font_size_px} !important;
        --bg-color: #0f172a;
        --text-color: #f1f5f9;
        --card-bg: #1e293b;
        --card-border: rgba(255, 255, 255, 0.1);
        --header-bg: linear-gradient(135deg, #001f3f 0%, #003366 100%);
        --header-text: #ffffff;
        --header-accent: #FFD700;
        --sub-header-color: #00FFCC;
        --result-bg: rgba(30, 41, 59, 0.5);
        --result-border: #1e3a8a;
        --badge-bg: rgba(0, 255, 204, 0.15);
        --badge-color: #00FFCC;
        --banner-bg: rgba(0, 128, 0, 0.15);
        --banner-border: #008000;
        --banner-title: #00FFCC;
        --input-err-color: #F87171;
        --logo-title-color: #FFD700;
        --divider-color: #1e3a8a;
    }}
    """
else:
    theme_variables = f"""
    :root {{
        font-size: {font_size_px} !important;
        --bg-color: #F0F2F5; /* Off-white government tint */
        --text-color: #1E293B; /* Slate dark text */
        --card-bg: #FFFFFF; /* Crisp white card background */
        --card-border: #CCCCCC; /* Solid grey borders */
        --header-bg: linear-gradient(135deg, #001f3f 0%, #003366 100%);
        --header-text: #FFFFFF;
        --header-accent: #FF9933;
        --sub-header-color: #FF9933;
        --result-bg: #f8fafc;
        --result-border: #003366;
        --badge-bg: rgba(0, 51, 102, 0.08);
        --badge-color: #003366;
        --banner-bg: rgba(0, 128, 0, 0.05);
        --banner-border: #008000;
        --banner-title: #006600;
        --input-err-color: #D32F2F;
        --logo-title-color: #003366;
        --divider-color: #003366;
    }}
    """

# Inject optimized CSS stylesheet overriding startup templates
st.markdown(f"""
<style>
    @import url('https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;500;600;700&display=swap');
    
    {theme_variables}
    
    /* Remove default Streamlit top padding and margins */
    .block-container {{
        padding-top: 0.5rem !important;
        padding-bottom: 1rem !important;
        padding-left: 2rem !important;
        padding-right: 2rem !important;
        max-width: 100% !important;
    }}
    
    /* Hide Streamlit top header and toolbar gap */
    header[data-testid="stHeader"] {{
        display: none !important;
    }}
    
    /* Ensure top header banner touches the top gracefully */
    div[data-testid="stVerticalBlock"] > div:first-child {{
        margin-top: 0rem !important;
    }}
    
    /* App background & font override */
    .stApp, html, body, [class*="css"] {{
        font-family: 'Outfit', sans-serif;
        background-color: var(--bg-color) !important;
        color: var(--text-color) !important;
        line-height: 1.6 !important;
        letter-spacing: 0.02rem !important;
        word-spacing: 0.05rem !important;
    }}
    
    /* Prevent Telugu and Urdu Unicode glyph clipping/overlapping */
    p, h1, h2, h3, h4, h5, h6, span, label, button, .stCheckbox, .stButton, div {{
        line-height: 1.6 !important;
        letter-spacing: 0.02rem !important;
    }}

    /* 1. High-Visibility Dark Input Boxes with Sky Blue Outlines */
    div[data-baseweb="textarea"] > div, 
    div[data-baseweb="input"] > div, 
    div[data-baseweb="select"] > div,
    .stTextArea textarea, 
    .stTextInput input {{
        background-color: #0F172A !important; /* Deep Dark Slate */
        border: 1.5px solid #38BDF8 !important; /* Visible Sky Blue Outline */
        border-radius: 8px !important;
        color: #F8FAFC !important;
        font-size: 15px !important;
        box-shadow: 0px 2px 6px rgba(0, 0, 0, 0.25) !important;
    }}
    
    /* Active Input Focus Glow */
    div[data-baseweb="textarea"]:focus-within > div, 
    div[data-baseweb="input"]:focus-within > div, 
    div[data-baseweb="select"]:focus-within > div {{
        border: 2px solid #FF6B00 !important; /* Saffron Focus Glow */
        box-shadow: 0px 0px 8px rgba(255, 107, 0, 0.4) !important;
    }}
    
    ::placeholder, textarea::placeholder, input::placeholder {{
        color: #94A3B8 !important;
        opacity: 0.85 !important;
    }}

    /* 2. Premium Telangana Navy & Saffron Buttons */
    .stButton > button {{
        background: linear-gradient(180deg, #003366 0%, #001F3F 100%) !important; /* Deep State Navy */
        color: #FFFFFF !important;
        border: 1px solid #38BDF8 !important; /* Sky Blue Border */
        border-radius: 8px !important;
        font-weight: 600 !important;
        font-size: 15px !important;
        padding: 10px 20px !important;
        box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.35) !important;
        transition: all 0.2s ease-in-out !important;
    }}

    .stButton > button:hover {{
        background: linear-gradient(180deg, #FF6B00 0%, #D95A00 100%) !important; /* Telangana Saffron Accent */
        border-color: #FFA500 !important;
        color: #FFFFFF !important;
        transform: translateY(-2px) !important;
        box-shadow: 0px 6px 14px rgba(255, 107, 0, 0.45) !important;
    }}

    /* 3. Primary Submit Buttons (Saffron High-Priority Action) */
    div[data-testid="stForm"] .stButton > button,
    .stButton > button[kind="primary"] {{
        background: linear-gradient(180deg, #FF6B00 0%, #C85100 100%) !important;
        border: 1px solid #FF8C00 !important;
        color: #FFFFFF !important;
    }}

    /* 4. Enclosed Card Containers with Orange Header Accents */
    div[data-testid="stForm"], 
    div[data-testid="stExpander"], 
    div[data-testid="element-container"]:has(div[class*="stVerticalBlockBorder"]),
    div[class*="stVerticalBlockBorder"],
    .stCard {{
        background-color: #1E293B !important; /* Dark Slate Elevation */
        border: 1px solid #334155 !important;
        border-top: 4px solid #FF6B00 !important; /* State Accent Strip */
        border-radius: 12px !important;
        padding: 18px !important;
        box-shadow: 0px 8px 16px rgba(0, 0, 0, 0.3) !important;
        margin-bottom: 16px !important;
    }}

    /* 5. Checkbox, Radio, and File Uploader Text Alignment */
    div[data-baseweb="checkbox"] label, 
    div[data-baseweb="radio"] label,
    .stFileUploader label {{
        color: #F1F5F9 !important;
        font-weight: 500 !important;
    }}

    /* Target all Streamlit Expander header text, titles, and icons */
    div[data-testid="stExpander"] summary,
    div[data-testid="stExpander"] summary p,
    div[data-testid="stExpander"] summary span,
    div[data-testid="stExpander"] details summary div {{
        color: #FFFFFF !important;
        font-weight: 600 !important;
        font-size: 16px !important;
    }}
    
    /* Expander Header Hover State */
    div[data-testid="stExpander"] summary:hover p,
    div[data-testid="stExpander"] summary:hover span {{
        color: #FF6B00 !important; /* Saffron highlight on hover */
    }}

    /* Force High-Contrast Pure White/Cyan for ALL Multiselect Input Placeholders */
    .stMultiSelect div[data-baseweb="select"] input::placeholder,
    .stMultiSelect div[data-baseweb="select"] input,
    .stMultiSelect div[data-baseweb="select"] div,
    .stMultiSelect [data-baseweb="select"] div,
    .stMultiSelect div[data-baseweb="select"],
    div[data-baseweb="select"] input::placeholder,
    div[data-baseweb="select"] input,
    div[data-baseweb="select"] div[role="button"] span,
    div[data-baseweb="select"] span {{
        color: #00F0FF !important; /* High-Visibility Electric Cyan / Pure White */
        -webkit-text-fill-color: #00F0FF !important; /* Force Webkit Override */
        opacity: 1 !important;
        font-weight: 600 !important;
        font-size: 15px !important;
    }}

    /* Ensure the Multiselect Box Background is Dark Slate with Sky Blue Outline */
    .stMultiSelect div[data-baseweb="select"] > div {{
        background-color: #0F172A !important;
        border: 1.5px solid #38BDF8 !important;
        border-radius: 8px !important;
    }}

    /* 2. Style Selected Tags Inside Multiselect */
    span[data-baseweb="tag"] {{
        background-color: #FF6B00 !important; /* Telangana Saffron Tag Fill */
        color: #FFFFFF !important;
        border-radius: 6px !important;
        font-weight: 600 !important;
    }}
    
    span[data-baseweb="tag"] * {{
        color: #FFFFFF !important;
        -webkit-text-fill-color: #FFFFFF !important;
    }}
    
    /* 3. Style Tag Close Icons */
    span[data-baseweb="tag"] span[role="button"] {{
        color: #FFFFFF !important;
    }}
    
    /* Emergency Banner */
    .emergency-desk {{
        background: var(--banner-bg);
        border: 2px solid var(--banner-border);
        border-radius: 12px;
        padding: 0.75rem;
        margin-top: 5px;
        margin-bottom: 1rem;
        text-align: center;
    }}
    
    .emergency-buttons-container {{
        display: flex;
        justify-content: center;
        gap: 1rem;
        flex-wrap: wrap;
    }}
    
    .emergency-btn {{
        display: inline-flex;
        align-items: center;
        gap: 0.5rem;
        background: linear-gradient(135deg, #003366 0%, #002b49 100%);
        border: 1px solid var(--header-accent);
        color: #ffffff !important;
        text-decoration: none !important;
        padding: 0.5rem 1.25rem;
        border-radius: 6px;
        font-weight: 600;
        font-size: 0.85rem;
        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        transition: all 0.2s ease;
    }}
    
    .emergency-btn:hover {{
        transform: translateY(-1px);
        box-shadow: 0 6px 12px rgba(255, 215, 0, 0.2);
        border-color: #00FFCC;
        background: linear-gradient(135deg, #138808 0%, #004d00 100%);
    }}

    /* Card styling conforming to form panels */
    .card {{
        background-color: var(--card-bg);
        border: 1px solid var(--card-border);
        border-radius: 8px;
        padding: 1.5rem;
        margin-bottom: 1.25rem;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.03);
    }}
    
    .card-title {{
        font-size: 1.1rem;
        font-weight: 700;
        margin-bottom: 0.5rem;
        display: flex;
        align-items: center;
        gap: 0.4rem;
        border-bottom: 1px solid var(--card-border);
        padding-bottom: 0.5rem;
    }}
    
    .card-desc {{
        font-size: 0.85rem;
        line-height: 1.4;
    }}
    
    /* Result Box card */
    .result-box {{
        background-color: var(--result-bg);
        border: 1px solid var(--result-border);
        border-radius: 8px;
        padding: 1.25rem;
        margin-top: 1rem;
    }}
    
    /* Supervisor badge */
    .supervisor-badge {{
        background-color: rgba(19, 136, 8, 0.1);
        color: #138808;
        border: 1px solid #138808;
        padding: 0.2rem 0.6rem;
        border-radius: 4px;
        font-weight: 700;
        display: inline-flex;
        align-items: center;
        gap: 0.3rem;
        font-size: 0.8rem;
    }}
    
    .supervisor-badge-warning {{
        background-color: rgba(255, 153, 51, 0.1);
        color: #ff9933;
        border: 1px solid #ff9933;
        padding: 0.2rem 0.6rem;
        border-radius: 4px;
        font-weight: 700;
        display: inline-flex;
        align-items: center;
        gap: 0.3rem;
        font-size: 0.8rem;
    }}
    
    /* PDF Preview Box */
    .pdf-preview {{
        background: #f8fafc;
        color: #0f172a;
        font-family: 'Courier New', Courier, monospace;
        border: 2px solid #cbd5e1;
        border-radius: 8px;
        padding: 1.5rem;
        margin-top: 1rem;
        box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
    }}
    
    .pdf-header {{
        text-align: center;
        border-bottom: 2px solid #0f172a;
        padding-bottom: 0.75rem;
        margin-bottom: 1.25rem;
    }}
    
    /* Badge styling */
    .badge {{
        display: inline-block;
        padding: 0.2rem 0.6rem;
        border-radius: 4px;
        font-size: 0.75rem;
        font-weight: 600;
        margin-right: 0.4rem;
        margin-bottom: 0.4rem;
    }}
    
    .badge-primary {{
        background-color: var(--badge-bg);
        color: var(--badge-color);
        border: 1px solid var(--card-border);
    }}

    /* PWA Mobile Optimization & Responsive Breakpoints */
    @media (max-width: 768px) {{
        /* Collapse columns to single-column blocks */
        .row-widget.stHorizontal, div[data-testid="column"] {{
            width: 100% !important;
            flex: 1 1 100% !important;
            margin-bottom: 1rem !important;
        }}
        /* Adjust font size for mobile readability */
        :root {{
            font-size: 15px !important;
        }}
        /* Scale padding for smaller screens */
        .block-container {{
            padding-left: 1rem !important;
            padding-right: 1rem !important;
        }}
        /* Enlarge tap targets for mobile touch */
        .stButton button, .emergency-btn {{
            padding: 0.75rem 1.5rem !important;
            font-size: 1rem !important;
            min-height: 48px !important;
        }}
        input, select, textarea {{
            font-size: 16px !important; /* Prevents auto-zoom on iOS */
            min-height: 48px !important;
        }}
    }}
</style>
""", unsafe_allow_html=True)

# Divider line separating utility strip from notice board
st.markdown("<hr style='border-top: 2px solid var(--divider-color); margin-top: 5px; margin-bottom: 5px;' />", unsafe_allow_html=True)

# Official Notice Ticker (Marquee Banner)
st.markdown(
    """
    <div style="background-color: #FF9933; color: #000000; font-size: 0.85rem; font-weight: 700; padding: 6px 15px; border-radius: 4px; overflow: hidden; margin-top: 5px; margin-bottom: 10px;">
        <marquee scrollamount="4" behavior="scroll" direction="left">
            ⚠️ Notice: For immediate emergency assistance, call 1100 or use the WhatsApp Assistant below. Prajavani Public Grievance Portal is fully GIGW compliant.
        </marquee>
    </div>
    """, unsafe_allow_html=True
)

# Quick Phone Preview box
local_ip = "127.0.0.1"
try:
    import socket
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    s.connect(("8.8.8.8", 80))
    local_ip = s.getsockname()[0]
    s.close()
except Exception:
    pass

mobile_url = f"http://{local_ip}:8501"
qr_api_url = f"https://api.qrserver.com/v1/create-qr-code/?size=150x150&data={mobile_url}"

with st.expander("📱 Quick Phone Preview & USB Connection"):
    col_qr, col_inst = st.columns([1, 3])
    with col_qr:
        st.image(qr_api_url, caption="Scan with Phone Camera", width=150)
    with col_inst:
        st.markdown(f"**Local Mobile Access URL:** [`{mobile_url}`]({mobile_url})")
        st.markdown("Point your phone camera at this QR code to open **Mana Telangana** directly on your device!")
        st.info("🔌 USB Cable Connected: Ensure 'USB Tethering' or 'MIDI/Transfer' is enabled in your phone's notification bar for direct high-speed preview!")

# 3. INTEGRATED QUICK-ACTION CHANNEL STRIP
st.markdown(
    """
    <div class="emergency-desk">
        <div class="emergency-buttons-container">
            <a href="https://wa.me/919999999999?text=Hi%20Mana%20Telangana" target="_blank" class="emergency-btn">
                🟢 WhatsApp Public Assistant
            </a>
            <a href="sms:+919999999999?body=HELP" class="emergency-btn">
                💬 Emergency SMS Gateway
            </a>
            <a href="tel:1100" class="emergency-btn">
                📞 Toll-Free Citizen Call Desk (1100)
            </a>
        </div>
    </div>
    """, unsafe_allow_html=True
)

# Multi-Tab Navigation Layout (Localized Strings)
tab1, tab2, tab3, tab4, tab5 = st.tabs([
    t["tab1"],
    t["tab2"],
    t["tab3"],
    t["tab4"],
    t["tab5"]
])

# =====================================================================
# TAB 1: AI CIVIC INTAKE & MEDIA UPLOADS
# =====================================================================
with tab1:
    # 4. STRUCTURED 2-COLUMN GRID LAYOUT (FORM & STATUS)
    col_in, col_status = st.columns([7, 5])
    
    with col_in:
        with st.container(border=True):
            st.markdown(f"##### 📝 {t['tab1']}")
            
            # Citizen Verification Tier Selector (from SIH-DRAFT-main Kotlin code)
            st.markdown("##### 🛡️ Citizen Verification Tier")
            verification_tier = st.radio(
                "Select verification security level:",
                ["Mobile OTP", "DigiLocker eKYC", "Zero-Knowledge Whistleblower Mode"],
                horizontal=True,
                key="citizen_verification_tier",
                label_visibility="collapsed"
            )
            
            st.markdown("---")
        
            grievance_input = st.text_area(
                t["grievance_label"],
                placeholder=t["grievance_placeholder"],
                height=130,
                key="civic_g_input"
            )
            
            # 1. LIVE VOICE & AUDIO INPUTS
            st.markdown(f"##### {t['voice_label']}")
            audio_file = st.audio_input(t["voice_instruction"])
            
            audio_props = None
            if audio_file is not None:
                audio_bytes = audio_file.getvalue()
                # Run computational header parser & checksum
                audio_props = verify_audio_evidence(audio_bytes)
                if audio_props:
                    st.markdown(
                        f"""
                        <div style="font-size:0.9rem; border:1px solid var(--banner-border); background-color: var(--banner-bg); padding:10px; border-radius:6px; margin-bottom:10px; color: var(--banner-title);">
                            <strong>✅ Voice Grievance Attached</strong><br>
                            <span style="font-size:0.8rem; opacity:0.9;">Secure Checksum: <code>{audio_props['checksum']}</code></span>
                        </div>
                        """, unsafe_allow_html=True
                    )
            
            # 3. FLEXIBLE MULTI-MODAL INTAKE (Show previews instantly before submit)
            st.markdown(f"##### {t['upload_label']}")
            media_file = st.file_uploader(
                t["upload_btn_label"],
                type=["jpg", "png", "mp4", "pdf"],
                key="civic_media_proof",
                help=t["upload_help"]
            )
            
            if media_file is not None:
                # Metadata calculation
                file_bytes = media_file.getvalue()
                size_formatted = format_file_size(len(file_bytes))
                ext_type = media_file.name.split('.')[-1].upper()
                timestamp_now = time.strftime("%Y-%m-%d %H:%M:%S IST")
                
                st.markdown(
                    f"""
                    <div style="font-size:0.85rem; border:1px solid var(--card-border); padding:8px; border-radius:4px; margin-bottom:10px;">
                        <strong>Name:</strong> {media_file.name}<br>
                        <strong>Format:</strong> {ext_type}<br>
                        <strong>File Size:</strong> {size_formatted}<br>
                        <strong>Verification Stamp:</strong> {timestamp_now}
                    </div>
                    """, unsafe_allow_html=True
                )
                
                # Live previews directly inside the form card
                if ext_type in ["JPG", "PNG", "JPEG"]:
                    st.image(media_file, caption="Uploaded Evidence Photo Preview", use_container_width=True)
                elif ext_type == "MP4":
                    st.video(media_file)
            
            # Screen/Audio Recording controls inside form boundary
            col_rec_b, col_rec_s = st.columns([1.2, 1.5])
            with col_rec_b:
                rec_btn = st.button(t["record_proof_btn"], key="rec_media_btn")
            with col_rec_s:
                if rec_btn:
                    st.session_state["media_recorded"] = True
                    with st.spinner("🎙 ..."):
                        time.sleep(1.2)
                    st.success("✅ Screen / Audio Proof successfully attached!")
                    
            # Voice Transcription Simulator
            voice_flag = st.checkbox(t["transcriber_checkbox"])
            if voice_flag:
                voice_sample = st.selectbox(
                    "Select Simulated Voice Sample:",
                    [
                        "Select...",
                        "Sample 1: Sewage leaking in Ameerpet (Telugu - తెలుగు)",
                        "Sample 2: High voltage wire Secunderabad (English)",
                        "Sample 3: Garbage pile block Nizamabad (Urdu - اردو)"
                    ]
                )
                if voice_sample != "Select...":
                    if "Ameerpet" in voice_sample:
                        grievance_input = "అమీర్‌పేట మెట్రో స్టేషన్ దగ్గర మురుగునీటి పైపు పగిలిపోయింది. HMWS&SB వారు వెంటనే చర్య తీసుకోవాలి."
                    elif "Secunderabad" in voice_sample:
                        grievance_input = "Dangerous high voltage power line hanging low in Secunderabad near post office."
                    elif "Nizamabad" in voice_sample:
                        grievance_input = "نظام آباد مین روڈ پر کچرے کا ڈھیر لگا ہوا ہے۔ بلدیہ جلد صفائی کروائے۔"
                    st.info(f"Transcribed Text: *\"{grievance_input}\"*")
                    
            # 5. STRICT FIELD VALIDATION & ERROR HANDLING
            intake_btn = st.button(t["submit_btn"], key="submit_official_grievance_btn")
        
    with col_status:
        with st.container(border=True):
            st.markdown(f"##### ⚙️ {t['supervisor_title']}")
        
        # Submit execution logic
        if intake_btn:
            # Reset RTI visibility
            st.session_state["show_rti_escalation"] = False
            
            resolved_text = grievance_input.strip()
            # If text is empty but audio is uploaded, run simulated STT dynamically
            if not resolved_text and audio_file is not None:
                resolved_text = transcribe_audio_simulation(audio_file.getvalue())
                
            # If Whistleblower mode is active, prepend the routing token (from SIH-DRAFT-main Kotlin code)
            if verification_tier == "Zero-Knowledge Whistleblower Mode" and resolved_text:
                resolved_text = f"[WHISTLEBLOWER_ANONYMOUS_ROUTING] {resolved_text}"
                
            # Block form submission if both text and voice fields are blank
            if not grievance_input.strip() and audio_file is None:
                st.error("⚠️ Grievance submission blocked: Both text grievance and audio recording are empty. Please provide at least one input channel.")
            # Voice processing validation check: block trivial greetings
            elif is_query_trivial(resolved_text):
                st.warning("⚠️ Insufficient details: Please describe your specific location and complaint in text or voice.")
            else:
                progress_bar = st.progress(0)
                status_lbl = st.empty()
                
                steps = [
                    ("1. Intake Validation", 25),
                    ("2. AI Extraction", 50),
                    ("3. QA Supervisor Audit", 75),
                    ("4. Final Department Dispatch", 100)
                ]
                
                for step_lbl, val in steps:
                    status_lbl.markdown(f"**Current Stage:** `{step_lbl}`")
                    time.sleep(0.3)
                    progress_bar.progress(val)
                    
                status_lbl.empty()
                
                # Execute dynamic parsing engine
                result = process_civic_complaint(
                    user_text=resolved_text,
                    audio_bytes=audio_file.getvalue() if audio_file else None
                )
                
                if result:
                    # 1. GEOSPATIAL DE-DUPLICATION ENGINE
                    dup_check = check_geospatial_duplicate(resolved_text, result["final_department"])
                    if dup_check["duplicate_found"]:
                        st.info(f"ℹ️ Master Ticket Found: {dup_check['citizen_count']} citizens have already reported this issue in {dup_check['landmark']}. Your grievance has been linked to Master Ticket #{dup_check['master_id']}. You will receive real-time SMS updates!")
                        
                    # 2. PERSISTENT STATE MANAGEMENT (Append payload into session ledger)
                    ticket_payload = {
                        "tracking_id": result["tracking_id"],
                        "timestamp": time.strftime("%d-%b-%Y %H:%M:%S IST"),
                        "grievance_text": resolved_text,
                        "language": result["detected_language"],
                        "location": result["location"],
                        "final_department": result["final_department"],
                        "severity": result["severity"],
                        "tags": result["tags"],
                        "audio_hash": audio_props["checksum"] if audio_props else "None",
                        "attachment_name": media_file.name if media_file else "None"
                    }
                    st.session_state['grievance_db'].append(ticket_payload)
                    st.session_state['active_ticket'] = ticket_payload
                    st.success("✅ AI Grievance Successfully Classified & Dispatched!")
        
        # 2. LIVE STATUS PANEL UPDATE (Renders from active ticket session state)
        active_t = st.session_state.get('active_ticket')
        
        if active_t:
            st.markdown('<div class="result-box">', unsafe_allow_html=True)
            st.markdown(f"<h5>Grievance Receipt: {active_t['tracking_id']}</h5>", unsafe_allow_html=True)
            
            # 2. CHAINED SLA ESCALATION CLOCK
            st.markdown(
                """
                <div style="background-color:rgba(211, 47, 47, 0.08); border-left:4px solid #D32F2F; padding:8px; border-radius:4px; margin-bottom:10px; font-size:0.85rem;">
                    <strong>⏳ SLA Countdown: 47 Hours 22 Minutes Remaining</strong><br>
                    <span style="font-size:0.75rem; color:#888;">Automated Direct Escalation to District Collector in 47h if unresolved.</span>
                </div>
                """, unsafe_allow_html=True
            )
            
            # Check supervisor validation loop audit dynamically
            audit_check = supervisor_validation_check(active_t['grievance_text'], active_t['final_department'])
            
            if audit_check["corrected"]:
                st.markdown(
                    f"""
                    <div style="margin-bottom:10px;">
                        <strong>Supervisor Audit Loop:</strong> 
                        <span class="supervisor-badge-warning">⚠️ REROUTED BY AUDITOR</span><br>
                        <span style="font-size:0.8rem; color:var(--text-color);">
                            <strong>Initial:</strong> {audit_check['initial_department']}<br>
                            <strong>Final:</strong> {audit_check['final_department']}<br>
                            <strong>Audit Log:</strong> {audit_check['audit_trail']}
                        </span>
                    </div>
                    """, unsafe_allow_html=True
                )
            else:
                st.markdown(
                    """
                    <div style="margin-bottom:10px;">
                        <strong>Supervisor Audit Loop:</strong> 
                        <span class="supervisor-badge">🛡️ APPROVED: Verified Routing Target</span>
                    </div>
                    """, unsafe_allow_html=True
                )
                
            st.markdown("---")
            st.markdown(f"**Extracted Location:** `{active_t['location']}`")
            st.markdown(f"**Severity Priority:** `{active_t['severity']}`")
            st.markdown(f"**Audio Evidence Checksum:** `{(active_t['audio_hash'])}`")
            
            st.markdown("**Grievance Tags:**")
            badges_html = "".join([f'<span class="badge badge-primary">{tag}</span>' for tag in active_t['tags']])
            st.markdown(badges_html, unsafe_allow_html=True)
            
            st.markdown("---")
            # 2. REAL PDF DOWNLOAD HANDLER
            col_d_act1, col_d_act2, col_d_act3 = st.columns(3)
            with col_d_act1:
                # Generate official receipt PDF dynamically on the fly
                pdf_bytes = generate_official_receipt_pdf(active_t)
                st.download_button(
                    label=t["download_pdf"],
                    data=pdf_bytes,
                    file_name=f"Telangana_Grievance_{active_t['tracking_id']}.pdf",
                    mime="application/pdf",
                    key="pdf_download_btn"
                )
            with col_d_act2:
                wa_msg = f"Grievance Registered ID: {active_t['tracking_id']}. Track status at: cgg.telangana.gov.in/verify?id={active_t['tracking_id']}"
                wa_url = f"https://wa.me/919999999999?text={wa_msg.replace(' ', '%20')}"
                st.markdown(
                    f'<a href="{wa_url}" target="_blank" class="emergency-btn" style="width:100%; justify-content:center; padding: 0.5rem 0.5rem;">{t["whatsapp_btn"]}</a>',
                    unsafe_allow_html=True
                )
            with col_d_act3:
                rti_escalate = st.button("⚖️ Escalate RTI", key="rti_escalate_trigger_btn")
                if rti_escalate:
                    st.session_state["show_rti_escalation"] = True
                    
            st.markdown("</div>", unsafe_allow_html=True)
            
            # 5. AUTOMATED RTI ESCALATOR DRAWER
            if st.session_state.get("show_rti_escalation"):
                rti_pdf = generate_rti_draft_pdf(active_t)
                st.success("⚖️ Legally binding RTI Application Drafted under Section 6(1)!")
                st.download_button(
                    label="📄 Download Certified RTI Application (PDF)",
                    data=rti_pdf,
                    file_name=f"Telangana_RTI_Escalation_{active_t['tracking_id']}.pdf",
                    mime="application/pdf",
                    key="rti_download_action_btn"
                )
        else:
            st.info("Submit your grievance on the left to activate G2C triage analysis and view dispatcher logs here.")

# =====================================================================
# TAB 2: ANTI-CORRUPTION BUREAU (ACB) PORTAL (ISOLATED TAB)
# =====================================================================
with tab2:
    st.markdown(f"### {t['acb_title']}")
    st.write(t["acb_desc"])
    
    col_whistle, col_v_audit = st.columns([1.1, 0.9])
    
    with col_whistle:
        with st.container(border=True):
            st.subheader(t["acb_form_header"])
            st.write(t["acb_form_desc"])
            
            acb_off_name = st.text_input(t["acb_officer_name"], placeholder=t["acb_officer_name_placeholder"], key="acb_off_name_f")
            acb_off_dept = st.selectbox(
                t["acb_dept"],
                ["Select Department...", "Revenue & Registration / Mutation Desk", "Electricity TSSPDCL", "Municipal Corporation GHMC", "Police & Law Enforcement", "Regional Transport Authority (RTA)"],
                key="acb_off_dept_f"
            )
            acb_bribe_amt = st.text_input(t["acb_amount"], placeholder="e.g. 20000", key="acb_bribe_amt_f")
            
            acb_incident_time = st.selectbox(
                t["acb_time_window"],
                ["Select Incident Window...", "Morning Slot (10:00 AM - 1:00 PM)", "Afternoon Slot (2:00 PM - 5:00 PM)", "Evening Slot (6:00 PM - 9:00 PM)"],
                key="acb_incident_time_f"
            )
            
            acb_off_loc = st.text_input(t["acb_location"], placeholder=t["acb_location_placeholder"], key="acb_off_loc_f")
            
            acb_ev_attached = st.multiselect(
                t["acb_evidence_types"],
                ['📹 Government Office CCTV Audit Request', '🎙️ Citizen Audio Recording', '📸 Photo Proof', '📄 Supporting WhatsApp Chat/Receipt'],
                default=[],
                key="acb_evidence_types_multiselect"
            )
            
            acb_doc = st.file_uploader(t["acb_upload_label"], type=["jpg", "png", "mp4", "pdf"], key="acb_portal_doc")
            
            acb_submit = st.button(t["acb_submit_btn"], key="submit_acb_form_btn")
            if acb_submit:
                if not acb_off_name.strip() or acb_off_dept == "Select Department..." or not acb_bribe_amt.strip() or acb_incident_time == "Select Incident Window...":
                    st.warning("⚠️ Please fill in all structured details (Officer, Department, Bribe Amount, and Incident Time Window) to register your complaint.")
                else:
                    cctv_requested = '📹 Government Office CCTV Audit Request' in acb_ev_attached
                    other_evidence = any(item != '📹 Government Office CCTV Audit Request' for item in acb_ev_attached) or (acb_doc is not None)
                    
                    audit_res = audit_acb_complaint_evidence(
                        officer_name=acb_off_name,
                        dept=acb_off_dept,
                        bribe_amount=acb_bribe_amt,
                        time_window=acb_incident_time,
                        evidence_files=other_evidence,
                        cctv_requested=cctv_requested
                    )
                    
                    if audit_res["status"] == "INSUFFICIENT_PROOF":
                        st.warning(f"🚫 **{audit_res['stage']}**\n\n{audit_res['message']}")
                    else:
                        zk_token = generate_zkp_token(f"{acb_off_name}-{acb_off_dept}-{acb_bribe_amt}-{acb_incident_time}")
                        st.success(f"🔓 **{audit_res['stage']}**\n\n{audit_res['message']}")
                        st.markdown(
                            f"""
                            <div class="result-box" style="border-color:#008000; margin-top:5px;">
                                <strong>Verification Process Stage Status:</strong><br>
                                <span>🟢 Stage 1: Validated</span> ➔ <span>🟢 Stage 2: CCTV Cross-Check Enroute (Time Window: {acb_incident_time})</span><br><br>
                                <strong>Dispatch Target:</strong> Anti-Corruption Bureau Headquarters & Department CCTV Audit Desk<br>
                                <strong>Secure Encrypted Hash:</strong> <code>{zk_token}</code><br><br>
                                <em>Your complaint has been successfully registered. Anonymous ZKP routing has been established.</em>
                            </div>
                            """, unsafe_allow_html=True
                        )
                
    with col_v_audit:
        with st.container(border=True):
            st.subheader(t["acb_audit_header"])
            st.write(t["acb_audit_desc"])
            
            col_v_b, col_v_a = st.columns(2)
            with col_v_b:
                before_file = st.file_uploader(t["acb_before_upload"], type=["jpg", "png"], key="acb_b_file")
                if before_file:
                    st.image(before_file, caption="Citizen Before Photo Proof", use_container_width=True)
            with col_v_a:
                after_file = st.file_uploader(t["acb_after_upload"], type=["jpg", "png"], key="acb_a_file")
                if after_file:
                    st.image(after_file, caption="Officer After Resolution Proof", use_container_width=True)
                    
            run_v_audit = st.button(t["acb_run_audit"], key="run_v_audit_action_btn")
            
            if run_v_audit:
                if before_file and after_file:
                    audit_res = verify_visual_audit(before_file.getvalue(), after_file.getvalue())
                    st.success(audit_res["message"])
                    st.markdown(
                        f"""
                        <div class="result-box" style="border-color:#008000; margin-top:5px;">
                            <strong>AI Audit status:</strong> APPROVED<br>
                            <strong>Details:</strong> {audit_res['details']}
                        </div>
                        """, unsafe_allow_html=True
                    )
                else:
                    st.warning("Please upload both Citizen 'Before' and Officer 'After' photos to run visual audit.")
                
        st.markdown("---")
        with st.container(border=True):
            st.markdown(f"##### {t.get('officer_audit_sec', 'Compliance & Audit Officers Desk')}")
            st.write("Before rejecting complaints as sub-judice, execute e-Courts validation check.")
            
            col_ec1, col_ec2 = st.columns(2)
            with col_ec1:
                ec_ticket_id = st.text_input("Grievance Ticket ID:", value="TS-PRJ-2026-881A", key="ec_t_id")
                ec_case_num = st.text_input("Court Case Number:", placeholder="e.g. WP-2026-991A", key="ec_c_num")
            with col_ec2:
                officer_action = st.selectbox("Officer Resolution Action:", ["Select...", "Approved - Repairs Started", "Rejected — Sub-Judice / Pending in Court"], key="off_action_select")
                
            if st.button("Submit Officer Resolution Status", key="submit_off_res_btn"):
                if officer_action == "Rejected — Sub-Judice / Pending in Court":
                    if not ec_case_num.strip():
                        st.error("⚠️ Case number is required to reject a ticket under Sub-Judice status.")
                    else:
                        court_status = verify_ecourts_subjudice(ec_ticket_id, ec_case_num)
                        if not court_status["subjudice"]:
                            st.error("🛑 Rejection Blocked: e-Courts Pre-Audit verified NO active lawsuit exists for this issue. Officer must process grievance on merits.")
                        else:
                            st.success(court_status["message"])
                            st.info(f"Lawsuit Details: {court_status['case_title']} at {court_status['court']} ({court_status['status']})")
                elif officer_action == "Approved - Repairs Started":
                    st.success("✅ Officer resolution approved. Ticket updated.")
                else:
                    st.warning("Please select a resolution action.")

# =====================================================================
# TAB 3: AI LEGAL AFFIDAVIT DESK
# =====================================================================
with tab3:
    st.markdown(f"### {t['affidavit_title']}")
    st.write(t["affidavit_desc"])
    
    # Initialize shadow transactions
    if 'shadow_transactions' not in st.session_state:
        st.session_state['shadow_transactions'] = {}
        
    # Recovery input field
    with st.container(border=True):
        st.markdown(f"##### 💾 {t.get('resume_shadow_lbl', 'Resume Saved Transaction via Shadow ID')}")
        recover_id = st.text_input("Enter Shadow ID:", placeholder="e.g. TS-SHADOW-2026-A1B2C", label_visibility="collapsed", key="shadow_recovery_field")
        if st.button(t.get("shadow_id_btn", "Recover Form Data"), key="recover_shadow_btn"):
            if recover_id.strip() in st.session_state['shadow_transactions']:
                saved_state = st.session_state['shadow_transactions'][recover_id.strip()]
                st.session_state["c_correct_name"] = saved_state["c_name"]
                st.session_state["c_typo_name"] = saved_state["wrong_name"]
                st.session_state["c_doc_type"] = saved_state["doc_type"]
                st.success("✅ Form State Restored successfully!")
                st.rerun()
            else:
                st.error("⚠️ Shadow ID not found or expired.")
            
    col_a1, col_a2 = st.columns([1, 1.2])
    
    with col_a1:
        with st.container(border=True):
            st.markdown(f"#### {t['affidavit_form_header']}")
            c_name = st.text_input(t["affidavit_true_name"], value="Kalyan Reddy", key="c_correct_name")
            wrong_name = st.text_input(t["affidavit_typo_name"], value="Kalyana Reddi", key="c_typo_name")
            doc_type = st.selectbox(t["affidavit_doc_type"], ["Aadhaar Card", "PAN Card", "Telangana Dharani Land Deed", "Voter ID"], key="c_doc_type")
            
            # Auto-generate dynamic Shadow ID for current values
            state_hash = hashlib.sha256(f"{c_name}-{wrong_name}-{doc_type}".encode()).hexdigest().upper()
            shadow_id = f"TS-SHADOW-2026-{state_hash[:5]}"
            st.session_state['shadow_transactions'][shadow_id] = {
                "c_name": c_name,
                "wrong_name": wrong_name,
                "doc_type": doc_type
            }
            st.caption(f"💾 Session Auto-Saved. Shadow Transaction ID: `{shadow_id}`")
            
            generate_aff_btn = st.button(t["affidavit_draft_btn"], key="c_aff_btn")
        
    with col_a2:
        st.markdown(f"#### {t['affidavit_preview_header']}")
        if generate_aff_btn:
            if not c_name.strip() or not wrong_name.strip():
                st.error("⚠️ Correct name and Typo name are required.")
            else:
                aff_text, pdf_bytes, tracking_no = generate_legal_affidavit(c_name, wrong_name, doc_type)
                st.markdown(
                    f"""
                    <div class="pdf-preview">
                        <pre style="white-space: pre-wrap; font-family: 'Courier New', Courier, monospace; font-size: 0.8rem; margin:0; color:#0f172a;">
{aff_text}
                        </pre>
                    </div>
                    """, unsafe_allow_html=True
                )
                
                # Dynamic PDF and WhatsApp dispatch option buttons
                col_d1, col_d2 = st.columns(2)
                with col_d1:
                    st.download_button(
                        label="📄 Download Official PDF (In-App)",
                        data=pdf_bytes,
                        file_name=f"Affidavit_{tracking_no}.pdf",
                        mime="application/pdf",
                        key="download_aff_pdf"
                    )
                with col_d2:
                    wa_message = f"Hi, my Correction Affidavit {tracking_no} has been registered under CGG Telangana. Verify at: cgg.telangana.gov.in/verify?id={tracking_no}"
                    wa_url = f"https://wa.me/?text={wa_message.replace(' ', '%20')}"
                    st.markdown(
                        f'<a href="{wa_url}" target="_blank" class="emergency-btn" style="width:100%; justify-content:center;">🟢 Dispatch via WhatsApp</a>',
                        unsafe_allow_html=True
                    )
        else:
            st.info("Input name parameters on the left and click 'Draft Affidavit Draft' to view the generated document.")

# =====================================================================
# TAB 4: STATE ECOSYSTEM CONNECTOR DESK
# =====================================================================
with tab4:
    st.markdown(f"### {t['ecosystem_title']}")
    st.write(t["ecosystem_desc"])
    
    col_e1, col_e2, col_e3 = st.columns(3)
    
    with col_e1:
        st.markdown(
            f"""
            <div class="card">
                <div class="card-title">👵 {t['pension_title']}</div>
                <div class="card-desc">{t['pension_desc']}</div>
            </div>
            """, unsafe_allow_html=True
        )
        t_id = st.text_input(t["pension_t_id"], placeholder="e.g. TS/10293847", key="t_id_input")
        m_num = st.text_input(t["pension_phone"], placeholder="e.g. 9988776655", key="m_num_input")
        
        valid_inputs = True
        
        # Strict validations
        if t_id:
            if not re.match(r"^TS/[0-9]{8}$", t_id.strip()):
                st.markdown('<p style="color:var(--input-err-color); font-size:0.8rem; margin:0;">⚠️ Invalid Treasury ID format. Format must be TS/XXXXXXXX</p>', unsafe_allow_html=True)
                valid_inputs = False
        else:
            valid_inputs = False
            
        if m_num:
            if not re.match(r"^[6-9][0-9]{9}$", m_num.strip()):
                st.markdown('<p style="color:var(--input-err-color); font-size:0.8rem; margin:0;">⚠️ Invalid Mobile Number. Must be a valid 10-digit Indian number.</p>', unsafe_allow_html=True)
                valid_inputs = False
        else:
            valid_inputs = False
            
        request_payslip_btn = st.button(t["pension_req_btn"], key="req_p_btn", disabled=not valid_inputs)
        
        if request_payslip_btn:
            st.success("📩 OTP Sent to WhatsApp!")
            st.session_state["p_otp_sent"] = True
            st.session_state["p_t_id"] = t_id.strip()
            st.session_state["p_m_num"] = m_num.strip()
                
        if st.session_state.get("p_otp_sent"):
            otp_val = st.text_input(t["pension_otp_lbl"], placeholder="XXXXXX", key="pension_otp_val")
            is_otp_valid = bool(re.match(r"^[0-9]{6}$", otp_val.strip()))
            
            verify_dispatch_btn = st.button(t["pension_verify_btn"], key="ver_p_btn", disabled=not is_otp_valid)
            
            if verify_dispatch_btn:
                # Generate payslip PDF
                pdf_bytes, tracking_no = generate_pensioner_payslip(st.session_state["p_t_id"], st.session_state["p_m_num"])
                
                st.success("✅ Payslip generated successfully!")
                
                # In-App PDF download and WhatsApp dispatch buttons
                col_d_p1, col_d_p2 = st.columns(2)
                with col_d_p1:
                    st.download_button(
                        label=t["pension_dl_btn"],
                        data=pdf_bytes,
                        file_name=f"Payslip_{st.session_state['p_t_id'].replace('/', '_')}_{tracking_no}.pdf",
                        mime="application/pdf",
                        key="download_payslip_pdf"
                    )
                with col_d_p2:
                    wa_message = f"Hi, my Pensioner Payslip {tracking_no} has been verified under IFMIS. Download link: cgg.telangana.gov.in/verify?id={tracking_no}"
                    wa_url = f"https://wa.me/?text={wa_message.replace(' ', '%20')}"
                    st.markdown(
                        f'<a href="{wa_url}" target="_blank" class="emergency-btn" style="width:100%; justify-content:center;">{t["pension_wa_btn"]}</a>',
                        unsafe_allow_html=True
                    )
                
    with col_e2:
        st.markdown(
            f"""
            <div class="card">
                <div class="card-title">🗺️ {t['land_title']}</div>
                <div class="card-desc">{t['land_desc']}</div>
            </div>
            """, unsafe_allow_html=True
        )
        kh_num = st.text_input(t["land_khata"], value="40192", key="kh_num_input")
        s_num = st.text_input(t["land_s_num"], placeholder="e.g. SY-SEC-104/A", key="s_num_input")
        dharani_issue = st.selectbox(
            t["land_issue"],
            ["Extent Mismatch", "Prohibited Land List (22A) Removal", "Pattadar Name Spelling Correction", "Land Mutation & Sub-Division Delay"],
            key="dharani_issue_select"
        )
        
        valid_land_inputs = True
        if kh_num:
            if not re.match(r"^[0-9]+$", kh_num.strip()):
                st.markdown('<p style="color:var(--input-err-color); font-size:0.8rem; margin:0;">⚠️ Invalid Khata Number. Must be numbers only.</p>', unsafe_allow_html=True)
                valid_land_inputs = False
        else:
            valid_land_inputs = False
            
        if s_num:
            if not re.match(r"^SY-[A-Z]{3}-[0-9]+/?[A-Z0-9]*$", s_num.strip().upper()):
                st.markdown('<p style="color:var(--input-err-color); font-size:0.8rem; margin:0;">⚠️ Invalid Survey Number format. Format must be SY-MANDAL-NUMBER (e.g. SY-SEC-104/A)</p>', unsafe_allow_html=True)
                valid_land_inputs = False
        else:
            valid_land_inputs = False
            
        audit_land_btn = st.button(t["land_audit_btn"], key="l_audit_btn", disabled=not valid_land_inputs)
        
        if audit_land_btn:
            land_res = reconcile_land_documents(dharani_issue, s_num, kh_num)
            if land_res["status"] == "Mismatch Detected":
                st.error(f"🚫 **Mismatch Status:** {land_res['code']}")
                st.write(land_res["message"])
                st.info(land_res["details"])
            else:
                st.success(f"🔓 **Status:** {land_res['status']}")
                st.write(land_res["message"])
                st.info(land_res["details"])
                
            # Allow downloading Land Report
            st.download_button(
                label=t["land_dl_btn"],
                data=land_res["pdf_bytes"],
                file_name=f"LandReport_{s_num.strip().replace('/', '_')}_{land_res['tracking_no']}.pdf",
                mime="application/pdf",
                key="download_land_pdf"
            )
                
    with col_e3:
        st.markdown(
            f"""
            <div class="card">
                <div class="card-title">🏥 {t['health_title']}</div>
                <div class="card-desc">{t['health_desc']}</div>
            </div>
            """, unsafe_allow_html=True
        )
        a_id = st.text_input(t["health_id"], placeholder="e.g. AAR-123456789", key="a_id_input")
        
        valid_a_id = True
        if a_id:
            if not re.match(r"^AAR-[0-9]{9}$", a_id.strip().upper()):
                st.markdown('<p style="color:var(--input-err-color); font-size:0.8rem; margin:0;">⚠️ Invalid Aarogyasri ID format. Format must be AAR-XXXXXXXXX (e.g., AAR-123456789)</p>', unsafe_allow_html=True)
                valid_a_id = False
        else:
            valid_a_id = False
            
        wallet_btn = st.button(t["health_check_btn"], key="a_wallet_btn", disabled=not valid_a_id)
        
        if wallet_btn:
            st.success("🏥 Aarogyasri Account Retrieved")
            st.markdown(
                """
                <div style="border: 1px solid rgba(0, 51, 102, 0.2); border-radius:8px; padding:10px; background-color: rgba(0,51,102,0.05); margin-bottom:10px;">
                    <strong>Holder Name:</strong> Laxmaiah Goud<br>
                    <strong>Card ID:</strong> Aarogyasri-Verified<br>
                    <strong>Remaining Insurance:</strong> ₹10,00,000
                </div>
                """, unsafe_allow_html=True
            )
            
            st.subheader(t["health_beds_header"])
            st.markdown(
                """
                <table style="width:100%; border-collapse: collapse; font-size:0.8rem;">
                    <thead>
                        <tr style="border-bottom: 2px solid #FFD700; text-align:left;">
                            <th>Hospital</th>
                            <th>ICU Beds</th>
                            <th>General</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr style="border-bottom: 1px solid rgba(0,0,0,0.05);">
                            <td>NIMS Punjagutta</td>
                            <td style="color:#ff3333; font-weight:bold;">1 / 15</td>
                            <td>24 / 120</td>
                        </tr>
                        <tr style="border-bottom: 1px solid rgba(0,0,0,0.05);">
                            <td>Gandhi Hospital</td>
                            <td style="color:#008000; font-weight:bold;">8 / 30</td>
                            <td>56 / 200</td>
                        </tr>
                    </tbody>
                </table>
                """, unsafe_allow_html=True
            )

# =====================================================================
# TAB 5: EXECUTIVE STATE DASHBOARD
# =====================================================================
with tab5:
    st.markdown(f"### {t['exec_title']}")
    st.write(t["exec_desc"])
    
    col_m1, col_m2, col_m3, col_m4 = st.columns(4)
    with col_m1:
        st.metric(t["exec_metric1"], "1,84,209", "+2,491 this week")
    with col_m2:
        st.metric(t["exec_metric2"], "94.8%", "+0.5%")
    with col_m3:
        st.metric(t["exec_metric3"], "4", "-12")
    with col_m4:
        st.metric(t["exec_metric4"], "283", "+18")
        
    st.markdown("---")
    
    col_dash_l, col_dash_r = st.columns([2, 1])
    
    with col_dash_l:
        st.subheader(t["exec_table_header"])
        st.markdown(
            """
            <table style="width:100%; border-collapse: collapse; font-size:0.85rem;">
                <thead>
                    <tr style="border-bottom: 2px solid #FFD700; text-align:left; background-color:#003366; color:#ffffff;">
                        <th style="padding:8px;">District</th>
                        <th style="padding:8px;">Registered</th>
                        <th style="padding:8px;">Resolved</th>
                        <th style="padding:8px; color:#FFD700;">Performance Score</th>
                    </tr>
                </thead>
                <tbody>
                    <tr style="border-bottom: 1px solid rgba(0,0,0,0.05);">
                        <td style="padding:8px;">Hyderabad</td>
                        <td style="padding:8px;">45,291</td>
                        <td style="padding:8px;">43,026</td>
                        <td style="padding:8px; color:#008000; font-weight:bold;">95.0%</td>
                    </tr>
                    <tr style="border-bottom: 1px solid rgba(0,0,0,0.05);">
                        <td style="padding:8px;">Rangareddy</td>
                        <td style="padding:8px;">32,192</td>
                        <td style="padding:8px;">30,260</td>
                        <td style="padding:8px; color:#008000; font-weight:bold;">94.0%</td>
                    </tr>
                    <tr style="border-bottom: 1px solid rgba(0,0,0,0.05);">
                        <td style="padding:8px;">Medchal-Malkajgiri</td>
                        <td style="padding:8px;">28,910</td>
                        <td style="padding:8px;">27,118</td>
                        <td style="padding:8px; color:#008000; font-weight:bold;">93.8%</td>
                    </tr>
                    <tr style="border-bottom: 1px solid rgba(0,0,0,0.05);">
                        <td style="padding:8px;">Warangal</td>
                        <td style="padding:8px;">14,892</td>
                        <td style="padding:8px;">14,296</td>
                        <td style="padding:8px; color:#008000; font-weight:bold;">96.0%</td>
                    </tr>
                    <tr>
                        <td style="padding:8px;">Nizamabad</td>
                        <td style="padding:8px;">10,291</td>
                        <td style="padding:8px;">9,120</td>
                        <td style="padding:8px; color:#ff3333; font-weight:bold;">88.6% (Under Review)</td>
                    </tr>
                </tbody>
            </table>
            """, unsafe_allow_html=True
        )
        
    with col_dash_r:
        st.subheader(t["exec_sla_header"])
        st.markdown(
            """
            <div class="card">
                <div class="card-title" style="color: #ff3333;">HMWS&SB</div>
                <div class="card-desc">Active tickets: <b>14,291</b><br>SLA status: <span style="color:#ff3333; font-weight:bold;">8.2 hrs avg delay</span></div>
            </div>
            <div class="card">
                <div class="card-title" style="color: #ffcc00;">GHMC</div>
                <div class="card-desc">Active tickets: <b>24,910</b><br>SLA status: <span style="color:#ffcc00; font-weight:bold;">2.4 hrs avg delay</span></div>
            </div>
            """, unsafe_allow_html=True
        )
        
    st.markdown("---")
    with st.container(border=True):
        st.subheader("🕵️ SLA KPI Compliance & Anomaly Audit Ledger")
        st.write("Real-time automated audit check identifying gaming or fake ticket closures.")
        
        # Simulate active officer closures list
        officers_list = {
            "OFF-2026-9912": [
                {"timestamp_epoch": 1785239100, "ip_address": "192.168.1.100"},
                {"timestamp_epoch": 1785239110, "ip_address": "192.168.1.100"},
                {"timestamp_epoch": 1785239120, "ip_address": "192.168.1.100"},
                {"timestamp_epoch": 1785239130, "ip_address": "192.168.1.100"},
                {"timestamp_epoch": 1785239140, "ip_address": "192.168.1.100"},
                {"timestamp_epoch": 1785239150, "ip_address": "192.168.1.100"},
            ],
            "OFF-2026-4421": [
                {"timestamp_epoch": 1785239100, "ip_address": "192.168.2.11"},
                {"timestamp_epoch": 1785239200, "ip_address": "192.168.2.12"},
                {"timestamp_epoch": 1785239300, "ip_address": "192.168.2.13"},
            ]
        }
        
        col_an1, col_an2 = st.columns(2)
        for off_id, tickets in officers_list.items():
            audit_res = audit_officer_kpi_anomalies(off_id, tickets)
            with col_an1 if off_id == "OFF-2026-9912" else col_an2:
                if audit_res["anomaly_found"]:
                    st.error(f"🚨 Officer: {off_id} — UNDER AUDIT (KPI Gaming Detected)")
                    st.markdown(
                        f"""
                        <div style="background-color:rgba(211, 47, 47, 0.08); border-left:4px solid #D32F2F; padding:10px; border-radius:4px; font-size:0.85rem; color:var(--text-color);">
                            <strong>Severity:</strong> {audit_res['severity']}<br>
                            <strong>Audit Flags:</strong><br>
                            {"<br>".join([f"- {log}" for log in audit_res['audit_logs']])}
                        </div>
                        """, unsafe_allow_html=True
                    )
                else:
                    st.success(f"✅ Officer: {off_id} — COMPLIANT")
                    st.markdown(
                        """
                        <div style="background-color:rgba(19, 136, 8, 0.08); border-left:4px solid #138808; padding:10px; border-radius:4px; font-size:0.85rem; color:var(--text-color);">
                            <strong>Severity:</strong> NORMAL<br>
                            <strong>Status:</strong> All closures match normal performance SLA bounds.
                        </div>
                        """, unsafe_allow_html=True
                    )

# --- 5. OFFICIAL GOVERNMENT FOOTER ---
st.markdown(
    f"""
    <div style="background-color: #002b49; border-top: 4px solid #FF9933; color: #ffffff; padding: 1.5rem; text-align: center; margin-top: 3rem; font-size: 0.85rem; border-radius: 6px 6px 0 0;">
        <p style="margin: 0 0 10px 0; font-weight: 700;">
            {t['footer_cgg']}
        </p>
        <p style="margin: 0; color: #a1a1a1;">
            {t['footer_links']}
        </p>
        <p style="margin-top: 10px; font-size: 0.75rem; color: #888;">
            {t['footer_copyright']}
        </p>
    </div>
    """, unsafe_allow_html=True
)
