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
    private Integer option_id;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(name = "content", nullable = false, length = 255)
    private String content;

    @Column(name="is_answer")
    private boolean is_answer;

}
