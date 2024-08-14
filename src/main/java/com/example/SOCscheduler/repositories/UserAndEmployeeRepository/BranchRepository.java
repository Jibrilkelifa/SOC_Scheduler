package com.example.SOCscheduler.repositories.UserAndEmployeeRepository;


import com.example.SOCscheduler.model.UserAndEmployee.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BranchRepository extends JpaRepository<Branch, Long> {
    Optional<Branch> findBranchById(Long id);


//    List<Branch> findBranchBySubProcess(SubProcess subProcess);

//    @Query("SELECT b FROM Branch b WHERE b.subProcess = (:subProcess)")
//    List<Branch> findAllBySubProcess(SubProcess subProcess);

//    Optional<Branch> findBranchByName(String name);
}
