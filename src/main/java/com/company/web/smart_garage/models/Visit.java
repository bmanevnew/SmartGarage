package com.company.web.smart_garage.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "visits")
@Data
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "visit_id")
    private long id;

    @Column(name = "date")
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @ManyToMany
    @JoinTable(
            name = "visits_repairs",
            joinColumns = {@JoinColumn(name = "visit_id")},
            inverseJoinColumns = {@JoinColumn(name = "repair_id")}
    )
    private Set<Repair> repairs;
}
