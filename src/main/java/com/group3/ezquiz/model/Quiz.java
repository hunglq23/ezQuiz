package com.group3.ezquiz.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "_quiz")

public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer quiz_id;

    @Column(name="code", nullable = false)
    private String code;

    @Column(name="title", nullable = false, length = 50)
    private String title;

    @Column(name="description", nullable = false, length=500)
    private String description;

    @Column(name="is_active", nullable = false, columnDefinition = "boolean default false")
    private boolean is_active;

    @Column(name="is_exam_only", nullable = false)
    private boolean is_exam_only;

    @Column(name="is_draft", nullable = false)
    private boolean is_draft;

    @Column(name = "created_by")
    private Integer created_by;
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Timestamp created_at;

    @Column(name = "updated_by")
    private Integer updated_by;
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Timestamp update_at;
}
