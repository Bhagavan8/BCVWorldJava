package com.bcv.world.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bcv.world.model.UserEmailStatus;

@Repository
public interface UserEmailStatusRepository extends JpaRepository<UserEmailStatus, String> {
}