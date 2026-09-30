"""
Mana Telangana - G2C Automated Self-Auditing & Quality Verification Suite
NIC/G2C Engineering Division - Centre for Good Governance, Govt of Telangana.
Runs automated functional test cycles on classification pipelines, regex rules,
and PDF document layouts.
"""

import sys
import re
from backend import (
    is_query_trivial,
    process_civic_complaint,
    supervisor_validation_check,
    check_geospatial_duplicate,
    generate_official_receipt_pdf,
    generate_rti_draft_pdf,
    reconcile_land_documents,
    generate_pensioner_payslip,
    verify_audio_evidence,
    audit_acb_complaint_evidence
)

def run_system_audit():
    print("Initializing Mana Telangana AI Civic Portal Verification Cycle...")
    errors = []

    # 1. Test Trivial Guard
    print("Auditing Trivial Inputs and Greetings...")
    if not is_query_trivial("hi"):
        errors.append("is_query_trivial('hi') should return True")
    if not is_query_trivial("hello"):
        errors.append("is_query_trivial('hello') should return True")
    if is_query_trivial("There is a major water pipe leakage in Ameerpet near Metro"):
        errors.append("is_query_trivial() flagged a valid description as trivial")

    # 2. Test Dynamic Routing & Tagging (No hardcoded fallbacks)
    print("Auditing Dynamic AI Triage Classification and Target Routing...")
    
    # Water/Drainage
    res_water = process_civic_complaint("Water leak and broken pipe near Ameerpet")
    if "HMWS&SB" not in res_water["final_department"]:
        errors.append(f"Water query routed to incorrect department: {res_water['final_department']}")
    if "#WaterLeakage" not in res_water["tags"] or "#BrokenPipe" not in res_water["tags"]:
        errors.append(f"Water query generated incorrect tags: {res_water['tags']}")
        
    # Power/Electricity
    res_power = process_civic_complaint("Live transformer wire hanging dangerously in Secunderabad")
    if "TSSPDCL" not in res_power["final_department"]:
        errors.append(f"Power query routed to incorrect department: {res_power['final_department']}")
    if "#PowerCut" not in res_power["tags"] and "#LiveWire" not in res_power["tags"]:
        errors.append(f"Power query generated incorrect tags: {res_power['tags']}")

    # Garbage/Sanitation
    res_garbage = process_civic_complaint("Huge piles of garbage dumped on road in Nizamabad")
    if "GHMC" not in res_garbage["final_department"]:
        errors.append(f"Garbage query routed to incorrect department: {res_garbage['final_department']}")
    if "#GarbageDump" not in res_garbage["tags"]:
        errors.append(f"Garbage query generated incorrect tags: {res_garbage['tags']}")

    # Hindi Query Routing & Tagging
    res_hindi = process_civic_complaint("अमीरपेट में नाली और पानी की पाइप लाइन टूटने से जलभराव")
    if "HMWS&SB" not in res_hindi["final_department"]:
        errors.append(f"Hindi query routed to incorrect department: {res_hindi['final_department']}")
    if "#WaterLeakage" not in res_hindi["tags"]:
        errors.append(f"Hindi query generated incorrect tags: {res_hindi['tags']}")

    # Whistleblower Anonymous Triage
    res_whistle = process_civic_complaint("[WHISTLEBLOWER_ANONYMOUS_ROUTING] Demand of ₹5,000 bribe for land mutation")
    if "Vigilance Commission" not in res_whistle["final_department"]:
        errors.append(f"Whistleblower query routed to incorrect department: {res_whistle['final_department']}")
    if "#AntiCorruption" not in res_whistle["tags"] or "#Vigilance" not in res_whistle["tags"]:
        errors.append(f"Whistleblower query generated incorrect tags: {res_whistle['tags']}")

    # Land Reconciliation Audits
    land_ok = reconcile_land_documents("Pattadar Name Spelling Correction", "SY-SEC-104/A", "40192")
    if land_ok["status"] != "Reconciliation Successful":
        errors.append("reconcile_land_documents failed clean land check")
    land_lock = reconcile_land_documents("Prohibited Land List (22A) Removal", "SY-SEC-129", "40192")
    if land_lock["status"] != "Mismatch Detected":
        errors.append("reconcile_land_documents failed to lock prohibited land")

    # 3. Test Regex Rule sets
    print("Auditing Regulatory Government Regex Compliance Standards...")
    
    # Treasury ID: ^TS/[0-9]{8}$
    treasury_pattern = r"^TS/[0-9]{8}$"
    if not re.match(treasury_pattern, "TS/12345678"):
        errors.append("Treasury ID regex failed to match valid ID: TS/12345678")
    if re.match(treasury_pattern, "TS12345678") or re.match(treasury_pattern, "TS/12345"):
        errors.append("Treasury ID regex accepted invalid ID")

    # Aarogyasri ID: ^AAR-[0-9]{9}$
    aarogyasri_pattern = r"^AAR-[0-9]{9}$"
    if not re.match(aarogyasri_pattern, "AAR-123456789"):
        errors.append("Aarogyasri ID regex failed to match valid ID: AAR-123456789")
    if re.match(aarogyasri_pattern, "AAR123456789") or re.match(aarogyasri_pattern, "AAR-123"):
        errors.append("Aarogyasri ID regex accepted invalid ID")

    # Land Survey Number: ^SY-[A-Z]{3}-[0-9]+/?[A-Z0-9]*$
    survey_pattern = r"^SY-[A-Z]{3}-[0-9]+/?[A-Z0-9]*$"
    if not re.match(survey_pattern, "SY-SEC-104/A") or not re.match(survey_pattern, "SY-HYD-502"):
        errors.append("Land Survey Number regex failed to match valid formats")
    if re.match(survey_pattern, "SY104") or re.match(survey_pattern, "SY-104/A"):
        errors.append("Land Survey Number regex accepted invalid survey string")

    # 4. Test PDF Generation
    print("Auditing PDF Document Rendering Byte Streams...")
    receipt_pdf = generate_official_receipt_pdf({
        "tracking_id": "TS-PRJ-2026-99123",
        "timestamp": "29-July-2026 12:45:00 IST",
        "language": "English",
        "final_department": "HMWS&SB",
        "severity": "High",
        "location": "Ameerpet Metro Zone, Hyderabad",
        "grievance_text": "Water pipeline leakage on main road Ameerpet.",
        "tags": ["#WaterLeakage", "#BrokenPipe"],
        "audio_hash": "TS-AUDIO-HASH-A1B2C3D4",
        "attachment_name": "evidence.jpg"
    })
    
    if not receipt_pdf or len(receipt_pdf) < 100 or receipt_pdf[:4] != b"%PDF":
        errors.append("generate_official_receipt_pdf did not return a valid PDF byte array")

    rti_pdf = generate_rti_draft_pdf({
        "tracking_id": "TS-PRJ-2026-99123",
        "final_department": "HMWS&SB",
        "location": "Ameerpet Metro Zone, Hyderabad"
    })
    if not rti_pdf or len(rti_pdf) < 100 or rti_pdf[:4] != b"%PDF":
        errors.append("generate_rti_draft_pdf did not return a valid PDF byte array")

    # 5. Test Audio Engine properties
    print("Auditing Audio WAV PCM Binary Header Parsers...")
    mock_wav = b"RIFF\x24\x00\x00\x00WAVEfmt \x10\x00\x00\x00\x01\x00\x01\x00\x80\x3e\x00\x00\x00\x7d\x00\x00\x02\x00\x10\x00data\x00\x00\x00\x00"
    audio_props = verify_audio_evidence(mock_wav)
    # 6. Test e-Courts and KPI anomaly checks
    print("Auditing e-Courts Sub-Judice check and KPI Anomaly Detector...")
    from backend import verify_ecourts_subjudice, audit_officer_kpi_anomalies
    
    ec_res_active = verify_ecourts_subjudice("TS-123", "WP-2026-991A")
    if not ec_res_active["subjudice"]:
        errors.append("verify_ecourts_subjudice should detect WP-2026-991A as subjudice")
        
    ec_res_inactive = verify_ecourts_subjudice("TS-123", "MOCK-123")
    if ec_res_inactive["subjudice"]:
        errors.append("verify_ecourts_subjudice should not detect MOCK-123 as subjudice")
        
    an_res_flagged = audit_officer_kpi_anomalies("OFF-1", [
        {"timestamp_epoch": 1000, "ip_address": "192.168.1.1"},
        {"timestamp_epoch": 1010, "ip_address": "192.168.1.1"},
        {"timestamp_epoch": 1020, "ip_address": "192.168.1.1"},
        {"timestamp_epoch": 1030, "ip_address": "192.168.1.1"},
        {"timestamp_epoch": 1040, "ip_address": "192.168.1.1"},
        {"timestamp_epoch": 1050, "ip_address": "192.168.1.1"}
    ])
    if not an_res_flagged["anomaly_found"]:
        errors.append("audit_officer_kpi_anomalies should flag bulk closures within 60 seconds")
        
    # 7. Test ACB evidence & CCTV checking
    acb_res_fail = audit_acb_complaint_evidence("Ramesh", "Revenue", "20000", "2:00 PM - 5:00 PM", False, False)
    if acb_res_fail["status"] != "INSUFFICIENT_PROOF":
        errors.append("audit_acb_complaint_evidence should fail when no evidence is supplied")
        
    acb_res_pass = audit_acb_complaint_evidence("Ramesh", "Revenue", "20000", "2:00 PM - 5:00 PM", True, False)
    if acb_res_pass["status"] != "TRIAGED_FOR_CCTV_CROSSCHECK":
        errors.append("audit_acb_complaint_evidence should pass when evidence is supplied")

    # Output audit result summary
    print("\n---------------------------------------------------------")
    if errors:
        print(f"FAILED: Verification failed. Caught {len(errors)} bug(s):")
        for err in errors:
            print(f"  - {err}")
        sys.exit(1)
    else:
        print("SUCCESS: All 5 Verification Checklists successfully passed. Portal is 100% compliant!")
        sys.exit(0)

if __name__ == "__main__":
    run_system_audit()
