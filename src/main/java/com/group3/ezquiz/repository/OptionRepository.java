package com.group3.ezquiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group3.ezquiz.model.Option;

public interface OptionRepository extends JpaRepository<Option, Integer> {

}
