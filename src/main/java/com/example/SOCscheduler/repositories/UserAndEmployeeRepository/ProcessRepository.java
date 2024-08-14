package com.example.SOCscheduler.repositories.UserAndEmployeeRepository;

import com.example.SOCscheduler.model.UserAndEmployee.Process;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface ProcessRepository extends JpaRepository<Process, Long> {
}
