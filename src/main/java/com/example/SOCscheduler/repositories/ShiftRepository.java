package com.example.SOCscheduler.repositories;

import com.example.SOCscheduler.model.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShiftRepository extends JpaRepository<Shift, Long> {
    List<Shift> findByShiftType(String shiftType);
}
