package com.example.SOCscheduler.model;


import lombok.Data;
import lombok.NoArgsConstructor;


import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@Entity
@Table(name = "userrs")
public class Userr {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    private String gender;
    private String location;
    private String phoneNumber;
    @Column(name = "is_available")
    private boolean isAvailable = true;
}
