package com.example.SOCscheduler.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "all_categories")

public class AllCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Column(columnDefinition = "TEXT",length = 500)
    private String name;

    @ManyToOne
    @JoinColumn(name = "sub_module_id")
    private SubModule subModule;

    public AllCategory(String name) {
        this.name = name;
    }

    public AllCategory(String name, SubModule subModule) {
        this.name = name;
        this.subModule = subModule;

    }

}
