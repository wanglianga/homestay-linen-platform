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
@Table(name = "checkout_record")
public class CheckoutRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "room_no", nullable = false, length = 20)
    private String roomNo;

    @Column(name = "cleaner_id", nullable = false)
    private Long cleanerId;

    @Column(name = "cleaner_name", nullable = false, length = 50)
    private String cleanerName;

    @Column(name = "guest_name", length = 50)
    private String guestName;

    @Column(name = "checkout_time", nullable = false)
    private LocalDateTime checkoutTime;

    @Column(length = 500)
    private String remark;

    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        if (checkoutTime == null) checkoutTime = LocalDateTime.now();
    }
}
