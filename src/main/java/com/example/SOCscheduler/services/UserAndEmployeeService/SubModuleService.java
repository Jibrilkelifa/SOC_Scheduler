package com.example.SOCscheduler.services.UserAndEmployeeService;

import com.example.SOCscheduler.exception.UserNotFoundException;
import com.example.SOCscheduler.model.SubModule;
import com.example.SOCscheduler.repositories.SubModuleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubModuleService {
    private final SubModuleRepository subModuleRepository;

    public SubModuleService(SubModuleRepository subModuleRepository) {
        this.subModuleRepository = subModuleRepository;
    }

    public SubModule addSubModule(SubModule subModule) {
        return subModuleRepository.save(subModule);
    }

    public List<SubModule> findAllSubModule() {
        return subModuleRepository.findAll();
    }

    public SubModule updateSubModule(SubModule subModule) {
        return subModuleRepository.save(subModule);
    }

    public SubModule findSubModuleById(Long id) {
        return subModuleRepository.findSubModuleById(id)
                .orElseThrow(() -> new UserNotFoundException("SubModule by id = " + id + " was not found"));
    }

    public void deleteSubModule(Long id) {
        subModuleRepository.deleteById(id);
    }
}
