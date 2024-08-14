package com.example.SOCscheduler.repositories.UserAndEmployeeRepository;

import com.example.SOCscheduler.model.UserAndEmployee.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findTeamById(Long id);

    Team findByExternalName(String name);
}
