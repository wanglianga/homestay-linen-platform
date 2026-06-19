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
@Table(name = "linen_track")
public class LinenTrack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "linen_bag_id", nullable = false)
    private Long linenBagId;

    @Column(name = "bag_code", nullable = false, length = 50)
    private String bagCode;

    @Column(name = "linen_type_name", length = 50)
    private String linenTypeName;

    @Column(name = "room_no", length = 20)
    private String roomNo;

    @Column(nullable = false, length = 30)
    private String action;

    @Column(name = "action_detail", length = 500)
    private String actionDetail;

    @Column(name = "operator_id", nullable = false)
    private Long operatorId;

    @Column(name = "operator_name", nullable = false, length = 50)
    private String operatorName;

    @Column(name = "operator_role", length = 30)
    private String operatorRole;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "location_from", length = 100)
    private String locationFrom;

    @Column(name = "location_to", length = 100)
    private String locationTo;

    @Column(name = "action_time", nullable = false)
    private LocalDateTime actionTime;

    @PrePersist
    protected void onCreate() {
        if (actionTime == null) actionTime = LocalDateTime.now();
    }
}
