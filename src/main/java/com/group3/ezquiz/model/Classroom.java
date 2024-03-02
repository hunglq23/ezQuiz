
package com.group3.ezquiz.model;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_classroom")
public class Classroom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // unique
    @Column(nullable = false, length = 10)
    private String code;

    @Column(nullable = false, length = 100)
    private String className;

    @Column(nullable = false)
    private Boolean isEnable;

    private String description;

    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Timestamp createdAt;

    @ManyToOne
    @JoinColumn(name = "created_id")
    private User creator;

    @ManyToMany
    @JoinTable(name = "class_joining", joinColumns = {
            @JoinColumn(name = "class_id", referencedColumnName = "id")
    }, inverseJoinColumns = {
            @JoinColumn(name = "user_id", referencedColumnName = "id")
    })
    private Set<User> members;
}