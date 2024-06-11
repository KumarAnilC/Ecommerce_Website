package com.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.entity.UserDetails;

public interface UserRepository extends JpaRepository<UserDetails, Integer>{

}
