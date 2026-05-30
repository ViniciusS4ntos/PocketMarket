package com.pocketmarket.usercards;

import com.pocketmarket.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserCardRepository extends JpaRepository<UserCard, UUID> {
    Page<UserCard> findByOwner(User user, Pageable pageable);
}
