package com.company.web.smart_garage.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.Set;

import static com.company.web.smart_garage.utils.Constants.NOT_ARCHIVED;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "update vehicles set is_archived = true where vehicle_id = ?")
@Where(clause = NOT_ARCHIVED)
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

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.REMOVE)
    private Set<Visit> visits;
}
