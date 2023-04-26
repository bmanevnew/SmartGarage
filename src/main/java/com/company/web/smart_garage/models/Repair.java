package com.company.web.smart_garage.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLDelete;

import static com.company.web.smart_garage.utils.Constants.FILTER_NAME;
import static com.company.web.smart_garage.utils.Constants.FILTER_PARAM;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@SQLDelete(sql = "update repairs set is_deleted = true where repair_id = ?")
@FilterDef(name = FILTER_NAME, parameters = @ParamDef(name = FILTER_PARAM, type = Boolean.class))
@Filter(name = FILTER_NAME, condition = "is_deleted = :" + FILTER_PARAM)
@Entity
@Table(name = "repairs")
public class Repair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "repair_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private double price;

    @Column(name = "is_deleted")
    private boolean isDeleted;
}
