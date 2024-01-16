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
    @Column(name = "optionId", nullable = false, unique = true) // Add this line
    private Integer optionId;

    @ManyToOne
    @JoinColumn(name = "questionId")
    private Question question;

    @Column(name = "content", nullable = false, length = 255)
    private String content;

    @Column(name = "isAnswer")
    private boolean isAnswer;

}
