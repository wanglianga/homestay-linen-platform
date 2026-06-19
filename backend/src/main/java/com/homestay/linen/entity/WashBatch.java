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
@Table(name = "wash_batch")
public class WashBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "batch_no", nullable = false, unique = true, length = 50)
    private String batchNo;

    @Column(name = "factory_id", nullable = false)
    private Long factoryId;

    @Column(name = "factory_name", nullable = false, length = 50)
    private String factoryName;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "total_bags")
    private Integer totalBags = 0;

    @Column(name = "total_quantity")
    private Integer totalQuantity = 0;

    @Column(name = "return_quantity")
    private Integer returnQuantity = 0;

    @Column(name = "loss_quantity")
    private Integer lossQuantity = 0;

    @Column(length = 500)
    private String remark;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        if (status == null) status = "洗涤中";
    }
}
