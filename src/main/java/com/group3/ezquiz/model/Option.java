package com.group3.ezquiz.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "_option")
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class Option {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Long optionId;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(name = "content", nullable = false, length = 255)
    private String content;

    @Column(name = "isAnswer")
    private boolean isAnswer;

}
