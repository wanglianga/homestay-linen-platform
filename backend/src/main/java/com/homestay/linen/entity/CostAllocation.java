package com.homestay.linen.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cost_allocation")
public class CostAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stat_date", nullable = false)
    private LocalDate statDate;

    @Column(name = "linen_type_id", nullable = false)
    private Long linenTypeId;

    @Column(name = "linen_type_name", nullable = false, length = 50)
    private String linenTypeName;

    @Column(name = "wash_count")
    private Integer washCount = 0;

    @Column(name = "wash_cost", nullable = false)
    private BigDecimal washCost = BigDecimal.ZERO;

    @Column(name = "damage_count")
    private Integer damageCount = 0;

    @Column(name = "damage_cost")
    private BigDecimal damageCost = BigDecimal.ZERO;

    @Column(name = "compensation_income")
    private BigDecimal compensationIncome = BigDecimal.ZERO;

    @Column(name = "factory_charge")
    private BigDecimal factoryCharge = BigDecimal.ZERO;

    @Column(name = "net_cost", nullable = false)
    private BigDecimal netCost = BigDecimal.ZERO;

    @Column(length = 500)
    private String remark;

    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
}
