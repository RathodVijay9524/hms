package com.vijay.User_Master.controller;

import com.vijay.User_Master.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
@lombok.RequiredArgsConstructor
public class WebController {

    private final LabTestService labTestService;
    private final PatientService patientService;
    private final LabOrderService labOrderService;
    private final DepartmentService departmentService;
    private final DoctorProfileService doctorProfileService;
    private final BillingService billingService;
    private final UserService userService;
    private final ReceptionService receptionService;
    private final PharmacyDispensingService pharmacyDispensingService;
    private final InventoryService inventoryService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/select-role")
    public String selectRole(Model model) {
        com.vijay.User_Master.config.security.CustomUserDetails user = com.vijay.User_Master.Helper.CommonUtils.getLoggedInUser();
        model.addAttribute("userName", user.getName());
        return "select-role";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPassword(@RequestParam("uid") Long uid, @RequestParam("token") String token, Model model) {
        model.addAttribute("uid", uid);
        model.addAttribute("token", token);
        return "reset-password";
    }

    @GetMapping("/verify-account")
    public String verifyAccount(@RequestParam("uid") Long uid, @RequestParam("code") String code, Model model) {
        model.addAttribute("uid", uid);
        model.addAttribute("code", code);
        return "verify-account";
    }

    // Home Page
    @GetMapping("/")
    public String home() {
        return "index";
    }

    // Dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Legacy fallback - ideally we redirect from here too if needed
        populateCommonDashboardStats(model);
        return "dashboard";
    }
    @GetMapping("/reception/dashboard")
    public String receptionDashboard(Model model) {
        com.vijay.User_Master.dto.reception.ReceptionStatsDTO stats = receptionService.getDashboardStats();
        model.addAttribute("todayReg", stats.getTodayRegistrations());
        model.addAttribute("pendingTokens", stats.getPendingTokens());
        model.addAttribute("appointmentsCount", stats.getTotalAppointments());
        model.addAttribute("avgWaitTime", stats.getAvgWaitTime());
        
        model.addAttribute("todayTokens", receptionService.getTodayTokens());
        model.addAttribute("upcomingAppointments", receptionService.getTodayAppointments());
        
        return "reception/dashboard";
    }

    @GetMapping("/reception/register")
    public String receptionRegister(Model model) {
        return "reception/register";
    }

    @GetMapping("/reception/patients")
    public String receptionPatients(Model model) {
        model.addAttribute("patients", receptionService.getAllPatients());
        return "reception/patients";
    }

    @GetMapping("/reception/tokens")
    public String receptionTokens(Model model) {
        model.addAttribute("tokens", receptionService.getTodayTokens());
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("doctors", doctorProfileService.getAllDoctorProfiles());
        return "reception/tokens";
    }

    @GetMapping("/reception/appointments")
    public String receptionAppointments(Model model) {
        model.addAttribute("appointments", receptionService.getTodayAppointments());
        model.addAttribute("stats", receptionService.getDashboardStats());
        return "reception/appointments";
    }

    @GetMapping("/reception/book")
    public String receptionBook(Model model) {
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "reception/book";
    }

    @GetMapping("/reception/schedule")
    public String receptionSchedule(Model model) {
        return "reception/schedule";
    }

    @GetMapping("/reception/enquiry")
    public String receptionEnquiry(Model model) {
        model.addAttribute("enquiries", receptionService.getAllEnquiries());
        return "reception/enquiry";
    }

    @GetMapping("/reception/visitors")
    public String receptionVisitors(Model model) {
        model.addAttribute("visitors", receptionService.getAllVisitorLogs());
        model.addAttribute("stats", receptionService.getDashboardStats());
        return "reception/visitors";
    }

    // --- Inventory Portal ---
    @GetMapping("/inventory/dashboard")
    public String inventoryDashboard(Model model) {
        return "inventory/dashboard";
    }

    @GetMapping("/inventory/procurement")
    public String inventoryProcurement(Model model) {
        return "inventory/procurement";
    }

    @GetMapping("/inventory/grn")
    public String inventoryGrn(Model model) {
        return "inventory/grn";
    }

    @GetMapping("/inventory/requisitions")
    public String inventoryRequisitions(Model model) {
        return "inventory/requisitions";
    }

    @GetMapping("/inventory/vendors")
    public String inventoryVendors(Model model) {
        return "inventory/vendors";
    }

    @GetMapping("/inventory/audits")
    public String inventoryAudits(Model model) {
        return "inventory/audits";
    }

    // --- Executive Intelligence & Analytics Portal (MIS) ---
    @GetMapping("/analytics/dashboard")
    public String analyticsDashboard(Model model) {
        return "analytics/dashboard";
    }

    @GetMapping("/analytics/clinical")
    public String analyticsClinical(Model model) {
        return "analytics/clinical";
    }

    @GetMapping("/analytics/financial")
    public String analyticsFinancial(Model model) {
        return "analytics/financial";
    }

    @GetMapping("/analytics/operational")
    public String analyticsOperational(Model model) {
        return "analytics/operational";
    }

    @GetMapping("/analytics/inventory")
    public String analyticsInventory(Model model) {
        return "analytics/inventory";
    }

    @GetMapping("/analytics/reports")
    public String analyticsReports(Model model) {
        return "analytics/reports";
    }

    // --- Healthcare AI Intelligence Layer 🔥 ---
    @GetMapping("/intelligence/hub")
    public String intelligenceHub(Model model) {
        return "intelligence/hub";
    }

    @GetMapping("/intelligence/predictive")
    public String intelligencePredictive(Model model) {
        return "intelligence/predictive";
    }

    @GetMapping("/intelligence/operations")
    public String intelligenceOperations(Model model) {
        return "intelligence/operations";
    }

    @GetMapping("/intelligence/genai")
    public String intelligenceGenAI(Model model) {
        return "intelligence/genai";
    }

    @GetMapping("/lab/dashboard")
    public String labDashboard(Model model) {
        model.addAttribute("pendingOrders", labOrderService.getPendingOrderCount());
        model.addAttribute("reportsReady", labOrderService.getReportsReadyCount());
        model.addAttribute("recentOrders", labOrderService.getRecentOrders(10));
        return "lab/dashboard";
    }

    @GetMapping("/lab/inventory")
    public String labInventory(Model model) {
        // Mock stock status
        model.addAttribute("lowStockCount", 4);
        return "lab/inventory";
    }

    @GetMapping("/lab/quality-control")
    public String labQualityControl(Model model) {
        // Equipment status
        return "lab/quality-control";
    }

    @GetMapping("/lab/outsourced")
    public String labOutsourced(Model model) {
        // External referrals
        return "lab/outsourced";
    }

    @GetMapping("/lab/analytics")
    public String labAnalytics(Model model) {
        // Efficiency metrics
        model.addAttribute("avgTAT", "1.4 Hours");
        return "lab/analytics";
    }

    @GetMapping("/doctor/dashboard")
    public String doctorDashboard(Model model) {
        // Placeholder clinical stats
        model.addAttribute("todayAppointments", 0);
        model.addAttribute("pendingReports", 0);
        model.addAttribute("criticalAlerts", 0);
        return "doctor/dashboard";
    }

    @GetMapping("/doctor/appointments")
    public String doctorAppointments() {
        return "doctor/appointments";
    }

    @GetMapping("/doctor/patients")
    public String doctorPatients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        model.addAttribute("patientsPage", patientService.getAllPatients(page, size));
        return "doctor/emr";
    }

    @GetMapping("/doctor/emr")
    public String doctorEmr(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        model.addAttribute("patientsPage", patientService.getAllPatients(page, size));
        return "doctor/emr";
    }

    @GetMapping("/doctor/prescriptions")
    public String doctorPrescriptions(Model model) {
        model.addAttribute("activeLink", "doc-rx");
        return "doctor/prescriptions";
    }

    @GetMapping("/doctor/teleconsult")
    public String doctorTeleconsult() {
        return "doctor/teleconsult";
    }

    @GetMapping("/doctor/ipd")
    public String doctorIpd() {
        return "doctor/ipd";
    }

    @GetMapping("/doctor/alerts")
    public String doctorAlerts() {
        return "doctor/alerts";
    }

    @GetMapping("/doctor/clinical-ai")
    public String clinicalAI(Model model) {
        // Mocking AI insights
        model.addAttribute("highRiskCount", 3);
        model.addAttribute("diagnosticConfidence", 92);
        return "doctor/clinical-ai";
    }

    @GetMapping("/doctor/surgeries")
    public String doctorSurgeries(Model model) {
        return "doctor/surgeries";
    }

    @GetMapping("/doctor/consults")
    public String doctorConsults() {
        return "doctor/consults";
    }

    @GetMapping("/doctor/analytics")
    public String doctorAnalytics(Model model) {
        // Productivity metrics
        model.addAttribute("patientVolume", 156);
        model.addAttribute("revenueContribution", "₹4.5L");
        return "doctor/analytics";
    }

    


    @GetMapping("/patient/dashboard")
    public String patientDashboard(Model model) {
        model.addAttribute("totalPatients", patientService.getPatientCount());
        // Placeholder for daily stats
        model.addAttribute("newPatientsToday", 0);
        model.addAttribute("pendingAppointments", 0);
        return "patient/dashboard";
    }

    @GetMapping("/patient/list")
    public String patientList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        model.addAttribute("patientsPage", patientService.getAllPatients(page, size));
        return "patient/list";
    }

    @GetMapping("/patient/register")
    public String patientRegister() {
        return "patient/register";
    }

    @GetMapping("/patient/emr-dashboard")
    public String patientEmrDashboard() {
        return "patient/emr-dashboard";
    }

    @GetMapping("/patient/appointments")
    public String patientAppointments() {
        return "patient/appointments";
    }

    @GetMapping("/patient/details/{id}")
    public String patientFullDetails(@org.springframework.web.bind.annotation.PathVariable Long id, Model model) {
        model.addAttribute("patient", patientService.getPatientById(id));
        return "patient/details";
    }

    @GetMapping("/owner/dashboard")
    public String ownerDashboard(Model model) {
        // Executive KPI Suite
        model.addAttribute("activeStaff", 156);
        model.addAttribute("todayAppointments", 42);
        model.addAttribute("todayRevenue", 125800.0);
        model.addAttribute("occupancyRate", 86);
        model.addAttribute("avgStay", "4.2 Days");
        model.addAttribute("satisfaction", 94);
        
        // Revenue Trend (Last 6 Months)
        model.addAttribute("revenueLabels", java.util.Arrays.asList("Jul", "Aug", "Sep", "Oct", "Nov", "Dec"));
        model.addAttribute("revenueData", java.util.Arrays.asList(650000, 720000, 810000, 780000, 850000, 920000));

        // Operational Pulse Feed
        List<Map<String, Object>> events = new java.util.ArrayList<>();
        events.add(createEvent("09:45 AM", "Admission", "Critical cardiac admission in Wing B", "danger"));
        events.add(createEvent("10:12 AM", "Inventory", "Blood Bank: O+ supply below threshold", "warning"));
        events.add(createEvent("11:30 AM", "Staff", "Dr. Sarah J. started shift handover", "info"));
        events.add(createEvent("01:15 PM", "Billing", "Large corporate clearance: ₹4.5L", "success"));
        model.addAttribute("recentEvents", events);

        // Department Performance
        List<java.util.Map<String, Object>> depts = new java.util.ArrayList<>();
        depts.add(createDeptMetric("Cardiology", 142, 12));
        depts.add(createDeptMetric("Neurology", 89, 5));
        depts.add(createDeptMetric("Pediatrics", 114, 8));
        depts.add(createDeptMetric("Orthopedic", 67, -2));
        model.addAttribute("deptPerformance", depts);

        return "owner/dashboard";
    }

    private java.util.Map<String, Object> createEvent(String time, String type, String desc, String severity) {
        java.util.Map<String, Object> event = new java.util.HashMap<>();
        event.put("time", time);
        event.put("type", type);
        event.put("description", desc);
        event.put("severity", severity);
        return event;
    }

    private java.util.Map<String, Object> createDeptMetric(String name, int volume, int growth) {
        java.util.Map<String, Object> dept = new java.util.HashMap<>();
        dept.put("name", name);
        dept.put("volume", volume);
        dept.put("growth", growth);
        return dept;
    }

    @GetMapping("/owner/departments")
    public String ownerDepartments(Model model) {
        Object depts = departmentService.getAllDepartments();
        model.addAttribute("departments", depts != null ? depts : java.util.Collections.emptyList());
        return "owner/departments";
    }

    @GetMapping("/owner/doctors")
    public String ownerDoctors(Model model) {
        Object docs = doctorProfileService.getAllDoctorProfiles();
        Object depts = departmentService.getAllDepartments();
        model.addAttribute("doctors", docs != null ? docs : java.util.Collections.emptyList());
        model.addAttribute("departments", depts != null ? depts : java.util.Collections.emptyList());
        return "owner/doctors";
    }

    @GetMapping("/owner/workers")
    public String ownerWorkers() {
        return "owner/workers";
    }

    @GetMapping("/pharmacy/dashboard")
    public String pharmacyDashboard(Model model) {
        model.addAttribute("dashboardStats", pharmacyDispensingService.getDashboardStats());
        return "pharmacy/dashboard";
    }

    @GetMapping("/pharmacy/prescriptions")
    public String pharmacyPrescriptions(Model model) {
        return "pharmacy/prescriptions";
    }

    @GetMapping("/pharmacy/inventory")
    public String pharmacyInventory(Model model) {
        model.addAttribute("inventoryItems", inventoryService.getAllInventoryItems(org.springframework.data.domain.PageRequest.of(0, 100)).getContent());
        return "pharmacy/inventory";
    }

    @GetMapping("/pharmacy/dispense")
    public String pharmacyDispense(@RequestParam(required = false) Long id, Model model) {
        if (id != null) {
            model.addAttribute("prescription", pharmacyDispensingService.getPrescriptionForDispensing(id));
        }
        return "pharmacy/dispense";
    }

    @GetMapping("/pharmacy/orders")
    public String pharmacyOrders(Model model) {
        return "pharmacy/orders";
    }

    @GetMapping("/pharmacy/history")
    public String pharmacyHistory(Model model) {
        model.addAttribute("dispensings", pharmacyDispensingService.getAllDispensings());
        return "pharmacy/history";
    }

    @GetMapping("/pharmacy/expiry")
    public String pharmacyExpiry(Model model) {
        // For now, reuse inventory but we could filter it specifically for expiring items if InventoryService had that
        model.addAttribute("inventoryItems", inventoryService.getAllInventoryItems(org.springframework.data.domain.PageRequest.of(0, 100)).getContent());
        return "pharmacy/expiry";
    }

    @GetMapping("/pharmacy/returns")
    public String pharmacyReturns(Model model) {
        return "pharmacy/returns";
    }

    @GetMapping("/nurse/dashboard")
    public String nurseDashboard(Model model) {
        // Placeholder stats for Nurse
        model.addAttribute("activePatients", 24);
        model.addAttribute("dueMedications", 8);
        model.addAttribute("pendingVitals", 5);
        model.addAttribute("criticalAlerts", 2);
        model.addAttribute("medSchedule", java.util.Collections.emptyList());
        model.addAttribute("patientWatchlist", java.util.Collections.emptyList());
        return "nurse/dashboard";
    }

    @GetMapping("/nurse/patients")
    public String nursePatients(Model model) {
        return "nurse/patients";
    }

    @GetMapping("/nurse/wards")
    public String nurseWards(Model model) {
        return "nurse/wards";
    }

    @GetMapping("/nurse/vitals")
    public String nurseVitals(Model model) {
        return "nurse/vitals";
    }

    @GetMapping("/nurse/medications")
    public String nurseMedications(Model model) {
        return "nurse/medications";
    }

    @GetMapping("/nurse/tasks")
    public String nurseTasks(Model model) {
        return "nurse/tasks";
    }

    @GetMapping("/nurse/handover")
    public String nurseHandover(Model model) {
        return "nurse/handover";
    }

    @GetMapping("/nurse/alerts")
    public String nurseAlerts(Model model) {
        return "nurse/alerts";
    }

    @GetMapping("/billing/dashboard")
    public String billingDashboard(Model model) {
        // Placeholder stats for Billing
        model.addAttribute("totalRevenue", "₹24.5L");
        model.addAttribute("pendingDues", "₹3.2L");
        model.addAttribute("todaysBills", 47);
        model.addAttribute("todayCollected", "₹1.8L");
        return "billing/dashboard";
    }

    @GetMapping("/billing/invoices")
    public String billingInvoices(Model model) {
        return "billing/invoices";
    }

    @GetMapping("/billing/create")
    public String billingCreate(Model model) {
        return "billing/create";
    }

    @GetMapping("/billing/pending")
    public String billingPending(Model model) {
        return "billing/pending";
    }

    @GetMapping("/billing/collections")
    public String billingCollections(Model model) {
        return "billing/collections";
    }

    @GetMapping("/billing/refunds")
    public String billingRefunds(Model model) {
        return "billing/refunds";
    }

    @GetMapping("/billing/dues")
    public String billingDues(Model model) {
        return "billing/dues";
    }

    @GetMapping("/billing/reports")
    public String billingReports(Model model) {
        return "billing/reports";
    }

    @GetMapping("/billing/charges")
    public String billingCharges() {
        return "billing/charges";
    }

    @GetMapping("/doctor/schedules")
    public String doctorSchedules() {
        return "doctor/schedules";
    }

    @GetMapping("/doctor/doctors")
    public String doctorDoctors(Model model) {
        Object docs = doctorProfileService.getAllDoctorProfiles();
        model.addAttribute("doctors", docs != null ? docs : java.util.Collections.emptyList());
        return "doctor/doctors";
    }

    private void populateCommonDashboardStats(Model model) {
        model.addAttribute("totalPatients", patientService.getPatientCount());
        model.addAttribute("totalTests", labTestService.getLabTestCount());
        model.addAttribute("pendingOrders", labOrderService.getPendingOrderCount());
        model.addAttribute("reportsReadyCount", labOrderService.getReportsReadyCount());
        model.addAttribute("recentOrders", labOrderService.getRecentOrders(5));
    }

    // Lab Management Pages
    @GetMapping("/lab/tests")
    public String labTests(Model model) {
        model.addAttribute("tests", labTestService.getAllLabTests());
        return "lab/tests";
    }

    @GetMapping("/lab/patients")
    public String labPatients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        model.addAttribute("patientsPage", patientService.getAllPatients(page, size));
        return "lab/patients";
    }

    @GetMapping("/lab/orders")
    public String labOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        model.addAttribute("ordersPage", labOrderService.getAllOrders(page, size));
        return "lab/orders";
    }

    @GetMapping("/lab/patients/{id}")
    public String patientDetails(@org.springframework.web.bind.annotation.PathVariable Long id, Model model) {
        model.addAttribute("patient", patientService.getPatientById(id));
        return "lab/patient-details";
    }

    @GetMapping("/doctor/patients/{id}")
    public String doctorPatientDetails(@org.springframework.web.bind.annotation.PathVariable Long id, Model model) {
        model.addAttribute("patient", patientService.getPatientById(id));
        return "doctor/patient-details";
    }

    @GetMapping("/admin/schedules")
    public String schedules() {
        return "admin/schedules";
    }

    @GetMapping("/lab/appointments")
    public String appointments() {
        return "lab/appointments";
    }

    @GetMapping("/admin/appointment-dashboard")
    public String appointmentDashboard() {
        return "admin/appointment-dashboard";
    }

    @GetMapping("/masters/charges")
    public String charges() {
        return "masters/charges";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        populateCommonDashboardStats(model);
        return "admin/dashboard";
    }


    @GetMapping("/admin/users")
    public String adminUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }

    @GetMapping("/owner/nurses")
    public String ownerNurses() {
        return "owner/nurses";
    }


    @GetMapping("/admin/roles")
    public String adminRoles(Model model) {
        return "admin/roles";
    }

    @GetMapping("/admin/permissions")
    public String adminPermissions() {
        return "admin/permissions";
    }

    @GetMapping("/admin/accounts")
    public String adminAccounts() {
        return "admin/accounts";
    }

    @GetMapping("/admin/hospitals")
    public String adminHospitals() {
        return "admin/hospitals";
    }

    @GetMapping("/admin/laboratories")
    public String adminLaboratories() {
        return "admin/laboratories";
    }

    @GetMapping("/admin/departments")
    public String adminDepartments(Model model) {
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "admin/departments";
    }

    @GetMapping("/admin/audit")
    public String adminAudit() {
        return "admin/audit";
    }

    @GetMapping("/admin/settings")
    public String adminSettings() {
        return "admin/settings";
    }


    @GetMapping("/owner/billing")
    public String ownerBilling(Model model) {
        model.addAttribute("stats", billingService.getBillingStats());
        model.addAttribute("recentBills", billingService.getAllBills().stream().limit(5).collect(java.util.stream.Collectors.toList()));
        return "admin/billing";
    }

    @GetMapping("/owner/charges")
    public String ownerCharges() {
        return "masters/charges";
    }

    @GetMapping("/admin/billing")
    public String billing() {
        return "admin/billing";
    }

    @GetMapping("/admin/invoice/{id}")
    public String invoice(@org.springframework.web.bind.annotation.PathVariable Long id, Model model) {
        model.addAttribute("billId", id);
        return "admin/invoice";
    }
}
