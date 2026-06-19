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
@Table(name = "room_restock")
public class RoomRestock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "restock_no", nullable = false, unique = true, length = 50)
    private String restockNo;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "room_no", nullable = false, length = 20)
    private String roomNo;

    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    @Column(name = "warehouse_name", nullable = false, length = 50)
    private String warehouseName;

    @Column(name = "linen_type_id", nullable = false)
    private Long linenTypeId;

    @Column(name = "linen_type_name", nullable = false, length = 50)
    private String linenTypeName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(length = 500)
    private String remark;

    @Column(name = "restock_time", nullable = false)
    private LocalDateTime restockTime;

    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        if (restockTime == null) restockTime = LocalDateTime.now();
    }
}
