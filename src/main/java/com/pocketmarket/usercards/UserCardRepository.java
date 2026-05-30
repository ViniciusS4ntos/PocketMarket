package com.pocketmarket.usercards;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserCardRepository extends JpaRepository<UserCard, UUID> {
}
