package com.group3.ezquiz.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "_option")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Option {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Long optionId;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(name = "text", nullable = false, length = 255)
    private String text;

    @Column(name = "isAnswer")
    private boolean isAnswer;

    @Override
    public String toString() {
        return "Option{" +
                "optionId=" + optionId +
                ", question=" + question + // Make sure the Question class has a proper toString() method
                ", text='" + text + '\'' +
                ", isAnswer=" + isAnswer +
                '}';
    }

}
