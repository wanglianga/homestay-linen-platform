package com.homestay.linen.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "damage_report")
public class DamageReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_no", nullable = false, unique = true, length = 50)
    private String reportNo;

    @Column(name = "linen_bag_id")
    private Long linenBagId;

    @Column(name = "bag_code", length = 50)
    private String bagCode;

    @Column(name = "linen_type_id", nullable = false)
    private Long linenTypeId;

    @Column(name = "linen_type_name", nullable = false, length = 50)
    private String linenTypeName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, length = 30)
    private String damageType;

    @Column(name = "is_guest_responsibility", nullable = false)
    private Boolean guestResponsibility = false;

    @Column(name = "is_factory_responsibility")
    private Boolean factoryResponsibility = false;

    @Column(name = "is_wear_and_tear")
    private Boolean wearAndTear = false;

    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "room_no", length = 20)
    private String roomNo;

    @Column(name = "guest_name", length = 50)
    private String guestName;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "compensation_amount")
    private BigDecimal compensationAmount = BigDecimal.ZERO;

    @Column(name = "factory_charge_amount")
    private BigDecimal factoryChargeAmount = BigDecimal.ZERO;

    @Column(name = "photos", length = 1000)
    private String photos;

    @Column(length = 500)
    private String remark;

    @Column(name = "reporter_id", nullable = false)
    private Long reporterId;

    @Column(name = "reporter_name", nullable = false, length = 50)
    private String reporterName;

    @Column(name = "manager_id")
    private Long managerId;

    @Column(name = "manager_name", length = 50)
    private String managerName;

    @Column(name = "approve_time")
    private LocalDateTime approveTime;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        if (status == null) status = "待审批";
    }
}
