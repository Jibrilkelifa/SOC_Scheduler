package com.example.SOCscheduler.repositories.UserAndEmployeeRepository;


import com.example.SOCscheduler.model.UserAndEmployee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

    public interface EmployeeRepository extends JpaRepository<Employee, Long> {
        Optional<Employee> findEmployeeByEmployeeId(Long id);
        List<Employee> findByEmployeeFullNameIgnoreCaseContaining(String empName);

        Employee findByEmployeeFullName(String employeeFullName);


        boolean existsBySupervisorId(Long supervisorId);


        List<Employee> findBySupervisorId(Long supervisorId);

        @Query("SELECT b FROM Employee b WHERE levenshtein(b.employeeFullName, :searchName) <= 3")
        List<Employee> findByNameLevenshtein(@Param("searchName") String searchName);





    }

