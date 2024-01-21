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
    private Integer quizId;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "title", nullable = false, length = 50)
    private String title;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean isActive;

    @Column(name = "is_exam_only", nullable = false)
    private Boolean isExamOnly;

    @Column(nullable = true, columnDefinition = "boolean default true")
    private Boolean isDraft;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Timestamp createdAt;

    private Long updatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Timestamp updateAt;
}
