package com.example.bankcards.util;

import com.example.bankcards.dto.user.CreateUserRequest;
import com.example.bankcards.dto.user.UpdateUserRequest;
import com.example.bankcards.dto.user.UserDto;
import com.example.bankcards.entity.user.Role;
import com.example.bankcards.entity.user.User;
import java.time.LocalDate;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-26T22:15:28+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Eclipse Adoptium)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto toDto(User user) {
        if ( user == null ) {
            return null;
        }

        UUID id = null;
        String email = null;
        String phoneNumber = null;
        String firstName = null;
        String lastName = null;
        Role role = null;
        LocalDate birthDate = null;
        String fullName = null;

        id = user.getId();
        email = user.getEmail();
        phoneNumber = user.getPhoneNumber();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        role = user.getRole();
        birthDate = user.getBirthDate();
        fullName = user.getFullName();

        UserDto userDto = new UserDto( id, email, phoneNumber, firstName, lastName, role, birthDate, fullName );

        return userDto;
    }

    @Override
    public User toEntity(CreateUserRequest request) {
        if ( request == null ) {
            return null;
        }

        User user = new User();

        user.setEmail( request.email() );
        user.setPhoneNumber( request.phoneNumber() );
        user.setFirstName( request.firstName() );
        user.setLastName( request.lastName() );
        user.setRole( request.role() );
        user.setBirthDate( request.birthDate() );

        return user;
    }

    @Override
    public void updateEntity(UpdateUserRequest request, User user) {
        if ( request == null ) {
            return;
        }

        user.setPhoneNumber( request.phoneNumber() );
        user.setFirstName( request.firstName() );
        user.setLastName( request.lastName() );
        user.setBirthDate( request.birthDate() );
    }
}
