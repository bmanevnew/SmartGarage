package com.company.web.smart_garage.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "update visits set is_archived = true where visit_id = ?")
@Where(clause = "is_archived = false")
@Entity
@Table(name = "visits")
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "visit_id")
    private Long id;

    @Column(name = "date")
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User visitor;

    @ManyToMany
    @JoinTable(
            name = "visits_repairs",
            joinColumns = @JoinColumn(name = "visit_id"),
            inverseJoinColumns = @JoinColumn(name = "repair_id")
    )
    private Set<Repair> repairs;

    public double getTotalCost() {
        double totalCost = 0;
        if (repairs != null && !repairs.isEmpty()) {
            for (Repair repair : repairs) {
                totalCost += repair.getPrice();
            }
        }
        return totalCost;
    }
}
