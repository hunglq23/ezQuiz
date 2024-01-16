package com.group3.ezquiz.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "_question")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer questionId;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<Option> options;

    // getter and setter for options

    public void addOption(Option option) {
        options.add(option);
        option.setQuestion(this);
    }

    public void removeOption(Option option) {
        options.remove(option);
        option.setQuestion(null);
    }

    @Column(name = "questionCode", nullable = false, length = 10)
    private String questionCode;

    @Column(name = "content", nullable = false, length = 500)
    private String content;

    @Column(name = "isActive", nullable = false)
    private boolean isActive;

    @Column(name = "createdBy")
    private Integer createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Timestamp createdAt;

    @Column(name = "updatedBy")
    private Integer updatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Timestamp updateAt;
}
