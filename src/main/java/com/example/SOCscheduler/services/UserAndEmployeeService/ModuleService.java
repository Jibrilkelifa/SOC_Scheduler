package com.example.SOCscheduler.services.UserAndEmployeeService;

import com.example.SOCscheduler.exception.ResourceNotFoundException;
import com.example.SOCscheduler.model.UserAndEmployee.Module;
import com.example.SOCscheduler.repositories.UserAndEmployeeRepository.ModuleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModuleService {
    private final ModuleRepository moduleRepository;

    public ModuleService(ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    public Module addModule(Module module) {

        return moduleRepository.save(module);
    }

    public List<Module> findAllModule() {
        return moduleRepository.findAll();
    }

    public Module updateModule(Module module) {
        return moduleRepository.save(module);
    }

    public Module findModuleById(Long id) {
        return moduleRepository.findModuleById(id)

                .orElseThrow(() -> new ResourceNotFoundException("Module by id = " + id + " was not found"));
    }

    public void deleteModule(Long id) {
        moduleRepository.deleteById(id)
        ;
    }

    public Module findModuleByName(String name) {
        return moduleRepository.findModuleByName(name).orElseThrow(() -> new ResourceNotFoundException("Module by name = " + name + " was not found"));
    }
}