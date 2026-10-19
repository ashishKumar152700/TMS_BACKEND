package com.tms.transportationManagementSystem.entity;

import com.tms.transportationManagementSystem.enums.LoadingConfirmationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "loading_confirmations",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_loading_confirmation_trip",
                columnNames = "trip_id"
        )
)
@Getter
@Setter
public class LoadingConfirmation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trip_id", nullable = false, unique = true)
    private Trip trip;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private LoadingConfirmationStatus status = LoadingConfirmationStatus.DRAFT;

    private LocalDateTime loadingStartTime;
    private LocalDateTime loadingEndTime;

    private Double actualGrossWeight;
    private Double tareWeight;
    private Double actualNetWeight;

    private Integer packageCount;
    private Integer palletCount;
    private String sealNumber;

    @Column(length = 2000)
    private String remarks;

    private String confirmedBy;
    private LocalDateTime confirmedAt;

    @Column(length = 2000)
    private String supervisorRemarks;
    private String reviewedBy;
    private LocalDateTime reviewedAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(
            mappedBy = "loadingConfirmation",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<LoadingConfirmationItem> items = new ArrayList<>();

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (status == null) {
            status = LoadingConfirmationStatus.DRAFT;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
