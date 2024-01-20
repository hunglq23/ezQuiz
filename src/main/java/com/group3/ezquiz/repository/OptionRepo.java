package com.group3.ezquiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.group3.ezquiz.model.Option;

@Repository
public interface OptionRepo extends JpaRepository<Option, Long> {

    Option findByContent(String content);

}
