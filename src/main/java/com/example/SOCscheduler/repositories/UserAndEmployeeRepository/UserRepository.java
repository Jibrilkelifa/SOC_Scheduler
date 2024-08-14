package com.example.SOCscheduler.repositories.UserAndEmployeeRepository;


import com.example.SOCscheduler.model.UserAndEmployee.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    void deleteUserById(Long id);

    Optional<User> findUserById(Long id);
    List<User> findUsersByRolesId(Long roleId);

//    User findByUserName(String username);
    
    

    @Query("SELECT u FROM User u WHERE u.username = ?1")
    public User findByUserName(String username);

    @Query("SELECT u FROM User u WHERE u.createdAt = ?1")
    public List<User> findByMonth(String createdAt);

    @Query("SELECT COUNT(u) FROM User u WHERE u.username LIKE %:searchString%")
    Long countByUsernameContaining(@Param("searchString") String searchString);

    User findByUsername(String username);

    //User findByEmployee(Employee employee);
    User findFirstByOrderByIdDesc();
}
