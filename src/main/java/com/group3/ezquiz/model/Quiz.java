package com.group3.ezquiz.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "_quiz")
@Builder
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 10)
    private String code;

    @Column(nullable = false, length = 64)
    private String title;

    @Column(nullable = true, columnDefinition = "boolean default true")
    private Boolean isDraft;

    @Column(nullable = true, columnDefinition = "boolean default true")
    private Boolean isActive;

    @Column(nullable = false)
    private Boolean isExamOnly;

    @Column()
    private String description;

    @ManyToOne
    @JoinColumn(name = "created_id")
    private User createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Timestamp createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Timestamp updatedAt;
}
