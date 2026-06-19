package com.homestay.linen.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "linen_bag")
public class LinenBag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bag_code", nullable = false, unique = true, length = 50)
    private String bagCode;

    @Column(name = "checkout_id", nullable = false)
    private Long checkoutId;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "room_no", nullable = false, length = 20)
    private String roomNo;

    @Column(name = "cleaner_id", nullable = false)
    private Long cleanerId;

    @Column(name = "cleaner_name", nullable = false, length = 50)
    private String cleanerName;

    @Column(name = "linen_type_id", nullable = false)
    private Long linenTypeId;

    @Column(name = "linen_type_name", nullable = false, length = 50)
    private String linenTypeName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "has_stain")
    private Boolean hasStain = false;

    @Column(name = "stain_remark", length = 500)
    private String stainRemark;

    @Column(name = "stain_photos", length = 1000)
    private String stainPhotos;

    @Column(name = "driver_id")
    private Long driverId;

    @Column(name = "driver_name", length = 50)
    private String driverName;

    @Column(name = "pickup_weight")
    private Integer pickupWeight;

    @Column(name = "pickup_time")
    private LocalDateTime pickupTime;

    @Column(name = "wash_batch_id")
    private Long washBatchId;

    @Column(name = "wash_batch_no", length = 50)
    private String washBatchNo;

    @Column(name = "factory_check_time")
    private LocalDateTime factoryCheckTime;

    @Column(name = "factory_quantity")
    private Integer factoryQuantity;

    @Column(name = "factory_remark", length = 500)
    private String factoryRemark;

    @Column(name = "return_quantity")
    private Integer returnQuantity;

    @Column(name = "return_time")
    private LocalDateTime returnTime;

    @Column(name = "warehouse_id")
    private Long warehouseId;

    @Column(name = "warehouse_name", length = 50)
    private String warehouseName;

    @Column(name = "warehouse_check_time")
    private LocalDateTime warehouseCheckTime;

    @Column(name = "warehouse_quantity")
    private Integer warehouseQuantity;

    @Column(name = "warehouse_remark", length = 500)
    private String warehouseRemark;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
        if (status == null) status = "待取件";
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }

    @Transient
    public List<String> getStainPhotoList() {
        if (stainPhotos == null || stainPhotos.isEmpty()) {
            return new ArrayList<>();
        }
        return List.of(stainPhotos.split(","));
    }
}
