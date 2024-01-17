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

    @Column(name="is_draft", nullable = false, columnDefinition = "boolean default true")
    private boolean is_draft;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User created_by;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Timestamp created_at;

    @ManyToOne
    @JoinColumn(name = "updated_by")
    private User updated_by;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Timestamp update_at;
}
