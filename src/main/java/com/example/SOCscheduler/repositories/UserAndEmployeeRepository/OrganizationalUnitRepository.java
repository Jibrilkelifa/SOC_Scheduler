package com.example.SOCscheduler.repositories.UserAndEmployeeRepository;


import com.example.SOCscheduler.model.UserAndEmployee.OrganizationalUnit;
import com.example.SOCscheduler.model.UserAndEmployee.SubProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrganizationalUnitRepository extends JpaRepository<OrganizationalUnit, Long> {
    Optional<OrganizationalUnit> findOrganizationalUnitById(Long id);


    List<OrganizationalUnit> findOrganizationalUnitBySubProcess(SubProcess subProcess);

    @Query("SELECT b FROM OrganizationalUnit b WHERE b.subProcess = (:subProcess)")
    List<OrganizationalUnit> findAllBySubProcess(SubProcess subProcess);

    Optional <OrganizationalUnit> findOrganizationalUnitByName(String name);
}
