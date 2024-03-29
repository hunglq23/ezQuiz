package com.group3.ezquiz.model;

import java.sql.Timestamp;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "_class_joining")

public class ClassJoining {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "learner_id")
  private User learner;

  @ManyToOne
  @JoinColumn(name = "class_id")
  private Classroom classroom;

  @Temporal(TemporalType.TIMESTAMP)
  @CreationTimestamp
  private Timestamp joinedAt;

  @Column(name = "_displayed_name")
  private String learnerDisplayedName;

  @Column(name = "_displayed_phone")
  private String learnerDisplayedPhone;

}
