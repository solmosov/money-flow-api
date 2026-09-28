package com.example.moneyflowapi.role;

import com.example.moneyflowapi.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "roles")
public class Role extends BaseEntity {

    private String name;


}
