package com.example.SOCscheduler.controller;

import com.example.SOCscheduler.model.Shift;
import com.example.SOCscheduler.repositories.ShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shifts")
public class ShiftController {
    @Autowired
    private ShiftRepository shiftRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('SMS_ADMIN')")
    public ResponseEntity<Shift> createShift(@RequestBody Shift shift) {
        Shift savedShift = shiftRepository.save(shift);
        return ResponseEntity.ok(savedShift);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('SMS_ADMIN')")
    public ResponseEntity<List<Shift>> getAllShifts() {
        List<Shift> shifts = shiftRepository.findAll();
        return ResponseEntity.ok(shifts);
    }
}

