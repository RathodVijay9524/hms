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

    @GetMapping("/login")
    public String login() {
        return "login";
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
        model.addAttribute("totalPatients", patientService.getPatientCount());
        model.addAttribute("totalTests", labTestService.getLabTestCount());
        model.addAttribute("pendingOrders", labOrderService.getPendingOrderCount());
        model.addAttribute("reportsReadyCount", labOrderService.getReportsReadyCount());
        model.addAttribute("recentOrders", labOrderService.getRecentOrders(5));
        return "dashboard";
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

    // Master Management
    @GetMapping("/masters/departments")
    public String departments(Model model) {
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "masters/departments";
    }

    @GetMapping("/masters/doctors")
    public String doctors(Model model) {
        model.addAttribute("doctors", doctorProfileService.getAllDoctorProfiles());
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "masters/doctors";
    }

    @GetMapping("/admin/workers")
    public String workers() {
        return "admin/workers";
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
