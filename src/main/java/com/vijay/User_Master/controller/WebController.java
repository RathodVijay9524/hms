package com.vijay.User_Master.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@lombok.RequiredArgsConstructor
public class WebController {

    private final com.vijay.User_Master.service.LabTestService labTestService;
    private final com.vijay.User_Master.service.PatientService patientService;
    private final com.vijay.User_Master.service.LabOrderService labOrderService;
    private final com.vijay.User_Master.service.DepartmentService departmentService;
    private final com.vijay.User_Master.service.DoctorProfileService doctorProfileService;
    private final com.vijay.User_Master.service.BillingService billingService;
    private final com.vijay.User_Master.service.UserService userService;

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
    @GetMapping("/lab/dashboard")
    public String labDashboard(Model model) {
        model.addAttribute("pendingOrders", labOrderService.getPendingOrderCount());
        model.addAttribute("reportsReady", labOrderService.getReportsReadyCount());
        model.addAttribute("recentOrders", labOrderService.getRecentOrders(10));
        return "lab/dashboard";
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
    public String doctorPatients() {
        return "doctor/patients";
    }

    @GetMapping("/doctor/prescriptions")
    public String doctorPrescriptions() {
        return "doctor/prescriptions";
    }

    @GetMapping("/reception/dashboard")
    public String receptionDashboard() {
        return "reception/dashboard";
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
        // Populate owner-specific stats
        model.addAttribute("activeStaff", 0); // Placeholder
        model.addAttribute("todayAppointments", 0); // Placeholder
        model.addAttribute("todayRevenue", 0.0); // Placeholder
        model.addAttribute("pendingDues", 0.0); // Placeholder
        model.addAttribute("staffOnDuty", java.util.Collections.emptyList()); // Placeholder
        return "owner/dashboard";
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
        // Placeholder stats for Pharmacy
        model.addAttribute("pendingPrescriptions", 0);
        model.addAttribute("lowStockCount", 0);
        model.addAttribute("todayDispensed", 0);
        model.addAttribute("expiringCount", 0);
        model.addAttribute("activePrescriptions", java.util.Collections.emptyList());
        model.addAttribute("watchlist", java.util.Collections.emptyList());
        return "pharmacy/dashboard";
    }

    @GetMapping("/nurse/dashboard")
    public String nurseDashboard(Model model) {
        // Placeholder stats for Nurse
        model.addAttribute("activePatients", 0);
        model.addAttribute("dueMedications", 0);
        model.addAttribute("pendingVitals", 0);
        model.addAttribute("criticalAlerts", 0);
        model.addAttribute("medSchedule", java.util.Collections.emptyList());
        model.addAttribute("patientWatchlist", java.util.Collections.emptyList());
        return "nurse/dashboard";
    }

    @GetMapping("/billing/dashboard")
    public String billingDashboard(Model model) {
        model.addAttribute("stats", billingService.getBillingStats());
        model.addAttribute("recentBills", billingService.getAllBills().stream().limit(5).collect(java.util.stream.Collectors.toList()));
        return "billing/dashboard";
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
