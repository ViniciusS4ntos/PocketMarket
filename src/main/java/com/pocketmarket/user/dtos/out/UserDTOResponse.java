package com.pocketmarket.user.dtos.out;

import com.pocketmarket.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserDTOResponse {

    private String name;
    private String email;
    private UserRole role = UserRole.USER;
    private Long credits;

}
