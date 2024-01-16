package com.group3.ezquiz;

import com.group3.ezquiz.payload.QuizRequest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class EzquizApplication {

	public static void main(String[] args) {
		SpringApplication.run(EzquizApplication.class, args);
		System.out.println(new BCryptPasswordEncoder().encode("1234"));
	}

}
