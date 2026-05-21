package com.pocketmarket.user.dtos.in;

import com.pocketmarket.enums.UserRole;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserDTORequest {

    private String name;
    private String email;
    private String password;

}
