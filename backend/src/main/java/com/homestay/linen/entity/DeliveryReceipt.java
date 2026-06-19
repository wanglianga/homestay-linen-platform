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
@Table(name = "delivery_receipt")
public class DeliveryReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "receipt_no", nullable = false, unique = true, length = 50)
    private String receiptNo;

    @Column(name = "wash_batch_id", nullable = false)
    private Long washBatchId;

    @Column(name = "wash_batch_no", length = 50)
    private String washBatchNo;

    @Column(name = "driver_id", nullable = false)
    private Long driverId;

    @Column(name = "driver_name", nullable = false, length = 50)
    private String driverName;

    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    @Column(name = "warehouse_name", nullable = false, length = 50)
    private String warehouseName;

    @Column(name = "delivery_time")
    private LocalDateTime deliveryTime;

    @Column(name = "receive_time")
    private LocalDateTime receiveTime;

    @Column(name = "total_bags")
    private Integer totalBags = 0;

    @Column(name = "delivered_quantity")
    private Integer deliveredQuantity = 0;

    @Column(name = "received_quantity")
    private Integer receivedQuantity = 0;

    @Column(name = "difference_quantity")
    private Integer differenceQuantity = 0;

    @Column(length = 500)
    private String remark;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        if (status == null) status = "配送中";
    }
}
