package com.example.SOCscheduler.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Data

@NoArgsConstructor
@Entity
@Table(name = "all_irregularities")
public class AllIrregularity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;
    @Column(nullable = false, length = 10000, columnDefinition = "TEXT")
    private String name;
    @ManyToOne
    @JoinColumn(name = "all_sub_category_id")
    private AllSubCategory allSubCategory;

    public AllIrregularity(String name, AllSubCategory allSubCategory) {
        this.name = name;
        this.allSubCategory = allSubCategory;
    }
}
