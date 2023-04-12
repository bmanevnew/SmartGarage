package com.company.web.smart_garage.models.vehicle;

import com.company.web.smart_garage.models.user.User;
import com.company.web.smart_garage.models.visit.Visit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicle_id")
    private Long id;

    @Column(name = "license_plate")
    private String licensePlate;

    @Column(name = "vin")
    private String vin;

    @Column(name = "production_year")
    private Integer productionYear;

    @Column(name = "model")
    private String model;

    @Column(name = "brand")
    private String brand;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;

    @OneToMany(mappedBy = "vehicle")
    private Set<Visit> visits;
}
