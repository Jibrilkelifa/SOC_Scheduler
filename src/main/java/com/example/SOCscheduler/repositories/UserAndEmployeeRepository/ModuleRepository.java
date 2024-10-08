package com.example.SOCscheduler.repositories.UserAndEmployeeRepository;


import com.example.SOCscheduler.model.UserAndEmployee.Module;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ModuleRepository extends JpaRepository<Module, Long> {
    Optional<Module> findModuleById(Long id);
    Optional<Module> findModuleByName(String name);
    Module findModuleByCode(String code);
}
