package com.group3.ezquiz.model;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "_UUIDquiz")
public class QuizUUID {

  public static final List<String> AVAILABLE_TYPES = List.of(
      "single-choice",
      "multiple-choice");
  public static final int MIN_TITLE_LENGTH = 4;
  public static final int MAX_TITLE_LENGTH = 16;
  public static final int MAX_DESCRIPTION_LENGTH = 32;
  public static final int MAX_IMAGE_URL_LENGTH = 500;

  @Id
  @GeneratedValue
  private UUID id;

  @Column(length = MAX_TITLE_LENGTH)
  private String title;

  @Column(nullable = false)
  private Boolean isDraft;

  @Column(nullable = false)
  private Boolean isEnable;

  @Column(nullable = false)
  private Boolean isExam;

  @Column(length = MAX_IMAGE_URL_LENGTH)
  private String quizImageUrl;

  @Column(length = MAX_DESCRIPTION_LENGTH)
  private String description;

  @ManyToOne
  @JoinColumn(nullable = false, name = "created_id")
  private User creator;

  @ManyToMany
  @JoinTable(name = "_quiz_question", joinColumns = @JoinColumn(name = "quiz_id"), inverseJoinColumns = @JoinColumn(name = "quest_id"))
  Set<Quest> questions;

  @Temporal(TemporalType.TIMESTAMP)
  @CreationTimestamp
  private Timestamp createdAt;

  @Temporal(TemporalType.TIMESTAMP)
  @UpdateTimestamp
  private Timestamp updatedAt;
}
