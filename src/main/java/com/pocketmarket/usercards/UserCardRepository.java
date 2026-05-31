package com.pocketmarket.usercards;

import com.pocketmarket.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserCardRepository extends JpaRepository<UserCard, UUID> {

    List<UserCard> findByOwner(User owner);

}