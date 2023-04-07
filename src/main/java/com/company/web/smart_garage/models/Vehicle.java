package com.company.web.smart_garage.models;

import com.company.web.smart_garage.models.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Table(name = "vehicles")
@Data
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicle_id")
    private long id;

    @Column(name = "license_plate")
    private String licensePlate;

    @Column(name = "vin")
    private String vin;

    @Column(name = "production_year")
    private short productionYear;

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
