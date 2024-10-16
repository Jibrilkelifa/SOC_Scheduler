package com.example.SOCscheduler.controller;

import com.example.SOCscheduler.model.Schedule;
import com.example.SOCscheduler.model.UserHoursDto;
import com.example.SOCscheduler.model.Userr;
import com.example.SOCscheduler.repositories.ScheduleRepository;
import com.example.SOCscheduler.repositories.UserrRepository;
import com.example.SOCscheduler.services.SchedulingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/schedules")
public class SchedulingController {
    @Autowired
    private SchedulingService schedulingService;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private UserrRepository userrRepository;
    @Value("${sms.api.url}")
    private String smsApiUrl;
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
//    @GetMapping("/user-hours")
//    @PreAuthorize("hasAnyRole('SMS_ADMIN','SMS_USER')")
//    public ResponseEntity<List<UserHoursDto>> getUserWeeklyHours(
//            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
//
//        List<Userr> users = userrRepository.findAll();
//        Map<Userr, Long> userHoursMap = schedulingService.verifyWeeklyWorkingHourss(users, startDate, endDate);
//
//        // Convert the map to a list of UserHoursDto
//        List<UserHoursDto> response = userHoursMap.entrySet().stream()
//                .map(entry -> new UserHoursDto(entry.getKey().getName(), entry.getValue()))
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(response);
//    }
@GetMapping("/user-hours")
@PreAuthorize("hasAnyRole('SMS_ADMIN','SMS_USER')")
public ResponseEntity<List<UserHoursDto>> getUserWeeklyHours(
        @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

    List<Userr> users = userrRepository.findAll();
    Map<Userr, Long> userHoursMap = schedulingService.verifyWeeklyWorkingHourss(users, startDate, endDate);

    // Set all users' hours to 200
    List<UserHoursDto> response = userHoursMap.entrySet().stream()
            .map(entry -> new UserHoursDto(entry.getKey().getName(), 200L))  // Overwriting hours with 200
            .collect(Collectors.toList());

    return ResponseEntity.ok(response);
}



    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('SMS_ADMIN','SMS_USER')")
    public ResponseEntity<List<Schedule>> getAllSchedules() {
        List<Schedule> schedules = schedulingService.getAllSchedules();
        return ResponseEntity.ok(schedules);
    }
    @GetMapping("/formatted")
    @PreAuthorize("hasAnyRole('SMS_ADMIN','SMS_USER')")
    public ResponseEntity<Map<LocalDate, Map<String, List<String>>>> getFormattedSchedules() {
        Map<LocalDate, Map<String, List<String>>> formattedSchedules = schedulingService.getFormattedSchedules();
        return ResponseEntity.ok(formattedSchedules);
    }


    @Scheduled(cron = "0 0 8 * * ?", zone = "Africa/Addis_Ababa")

// Set to run at 6 PM every day
    public void sendNotificationToAllAnalysts() {
        LocalDate nextDay = LocalDate.now(); // Get the next day's date
        List<Schedule> schedules = scheduleRepository.findByDate(nextDay); // Fetch the schedules for the next day

        if (schedules.isEmpty()) {
            System.out.println("No schedules found for tomorrow.");
            return; // Exit if there are no schedules
        }

        // Create a message containing the schedule details
        String message = createScheduleMessage(schedules);

        // List of phone numbers to send the SMS to
        List<String> phoneNumbers = Arrays.asList("0912357931", "0910084446", "0921248393", "0942094473");

        // Send SMS to each phone number in the list
        RestTemplate restTemplate = new RestTemplate();
        for (String phoneNumber : phoneNumbers) {
            String url = smsApiUrl + "&to=" + phoneNumber + "&text=" + message;
            try {
                restTemplate.getForObject(url, String.class);
                System.out.println("SMS sent to " + phoneNumber);
            } catch (Exception e) {
                // Handle error (e.g., log it)
                System.err.println("Error sending SMS to " + phoneNumber + ": " + e.getMessage());
            }
        }
    }

    // Method to create a message from the list of schedules
    private String createScheduleMessage(List<Schedule> schedules) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("SOC Schedule for ").append(LocalDate.now()).append(":\n");

        for (Schedule schedule : schedules) {
            if (schedule != null && schedule.getUser() != null && schedule.getShift() != null) {
                messageBuilder.append("Analyst: ").append(schedule.getUser().getName())
                        .append(" -> ").append(schedule.getShift().getName())
                        .append("\n");
            } else {
                System.err.println("Found a null schedule, user, or shift.");
            }
        }
        return messageBuilder.toString();
    }




}



