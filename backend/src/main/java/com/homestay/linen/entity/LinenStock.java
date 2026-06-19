package com.homestay.linen.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "linen_stock")
public class LinenStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "linen_type_id", nullable = false, unique = true)
    private Long linenTypeId;

    @Column(name = "linen_type_name", nullable = false, length = 50)
    private String linenTypeName;

    @Column(nullable = false)
    private Integer totalQuantity = 0;

    @Column(name = "in_use_quantity")
    private Integer inUseQuantity = 0;

    @Column(name = "in_warehouse_quantity")
    private Integer inWarehouseQuantity = 0;

    @Column(name = "in_wash_quantity")
    private Integer inWashQuantity = 0;

    @Column(name = "in_delivery_quantity")
    private Integer inDeliveryQuantity = 0;

    @Column(name = "damage_quantity")
    private Integer damageQuantity = 0;

    @Column(name = "safety_stock", nullable = false)
    private Integer safetyStock = 50;

    @Column(name = "last_restock_time")
    private LocalDateTime lastRestockTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
