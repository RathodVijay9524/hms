---
description: how to implement and maintain EMR Clinical Safety and Documentation
---

# EMR Clinical Safety & Documentation Workflow

This workflow describes how to maintain and extend the clinical safety layer (Allergies, Vitals) and the PDF documentation engine.

## 1. Modifying Vital Sign Thresholds
The thresholds for clinical alerts are defined in `patient-details.html`.

1. Open `src/main/resources/templates/lab/patient-details.html`.
2. Locate the `VITAL_THRESHOLDS` constant:
```javascript
const VITAL_THRESHOLDS = {
    BP_SYSTOLIC: { min: 90, max: 150 },
    BP_DIASTOLIC: { min: 60, max: 95 },
    HEART_RATE: { min: 50, max: 110 },
    TEMP: { min: 96.0, max: 100.4 },
    OXYGEN: { min: 94, max: 100 }
};
```
3. Update the `min` or `max` values as per hospital policy.

## 2. Extending Allergy Logic
The allergy cross-check happens in real-time as a doctor types a medication name.

1. The logic is handled by the `checkAllergy(input)` function in `patient-details.html`.
2. To improve matching (e.g., adding drug class detection), update this function to check against a drug class mapping.

## 3. Customizing PDF Reports
PDFs are generated using Thymeleaf and OpenHtmlToPdf.

1. **Templates**: 
   - Prescription: `src/main/resources/templates/reports/prescription-report.html`
   - Visit Summary: `src/main/resources/templates/reports/visit-summary.html`
2. **Backend**:
   - `PdfExportServiceImpl.java` fetches the data and processes the template.
   - `EMRReportController.java` provides the secure download endpoint.

## 4. Verification Steps
After any change to these features, verify:
- [ ] **Allergy Trigger**: Type an allergic substance in "Record Visit" -> Row should show a red warning.
- [ ] **Vital Alerts**: Enter vitals outside thresholds -> Input field and Sidebar summary should turn red.
- [ ] **PDF Export**: Click "Print Rx" and verify the layout, hospital branding, and dynamic data (Patient Name, Age, etc.) are correct.
