package com.example.SOCscheduler.repositories.UserAndEmployeeRepository;

import com.example.SOCscheduler.model.UserAndEmployee.WorkCenter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkCenterRepository extends JpaRepository<WorkCenter, Long> {
}
