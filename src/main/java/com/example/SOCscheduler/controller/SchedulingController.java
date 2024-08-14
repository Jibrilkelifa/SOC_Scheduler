package com.example.SOCscheduler.controller;

import com.example.SOCscheduler.model.Schedule;
import com.example.SOCscheduler.repositories.ScheduleRepository;
import com.example.SOCscheduler.services.SchedulingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/schedules")
public class SchedulingController {
    @Autowired
    private SchedulingService schedulingService;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('SMS_ADMIN')")
    public ResponseEntity<Map<String, String>> generateSchedule(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam int days,
            @RequestParam boolean isThreeShiftScenario) {
        Map<String, String> response = new HashMap<>();
        try {
            schedulingService.generateSchedule(startDate, days, isThreeShiftScenario);
            response.put("status", "success");
            response.put("message", "Schedule generated successfully!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }


    @DeleteMapping("/deleteAll")
    @PreAuthorize("hasAnyRole('SMS_ADMIN')")
    public ResponseEntity<Void> deleteAllSchedules() {
        schedulingService.deleteAllSchedules();
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('SMS_ADMIN')")
    public ResponseEntity<List<Schedule>> getAllSchedules() {
        List<Schedule> schedules = schedulingService.getAllSchedules();
        return ResponseEntity.ok(schedules);
    }
    @GetMapping("/formatted")
    @PreAuthorize("hasAnyRole('SMS_ADMIN')")
    public ResponseEntity<Map<LocalDate, Map<String, List<String>>>> getFormattedSchedules() {
        Map<LocalDate, Map<String, List<String>>> formattedSchedules = schedulingService.getFormattedSchedules();
        return ResponseEntity.ok(formattedSchedules);
    }
}

