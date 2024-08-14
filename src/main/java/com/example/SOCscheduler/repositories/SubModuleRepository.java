package com.example.SOCscheduler.repositories;


import com.example.SOCscheduler.model.SubModule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubModuleRepository extends JpaRepository<SubModule, Long> {
    Optional<SubModule> findSubModuleById(Long id);
}
