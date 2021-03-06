package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.AdminBuisnessService;
import com.upgrad.quora.service.business.AuthTokenService;
import com.upgrad.quora.service.business.UserService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/")
public class AdminController {

    @Autowired
    UserService userService;

    @Autowired
    AdminBuisnessService adminBuisnessService;

    @Autowired
    AuthTokenService authTokenService;

    // this method deletes an existing user
    @DeleteMapping(path = "admin/user/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDeleteResponse> deleteUser(@PathVariable(name = "userId") final String uuid,
                                                         @RequestHeader("authorization") final String authToken) throws AuthorizationFailedException, UserNotFoundException {

        String token = getToken(authToken); // this method extracts the token from the JWT token string sent in the Request Header

        UserEntity userEntity = authTokenService.checkAuthentication(token, "deleteUser"); // checks if the auth token is valid or not

        if (userEntity.getRole().compareTo("admin") == 0) { // checks if the user role is admin type
            UserEntity deletedUser = adminBuisnessService.deleteUser(uuid);
            return new ResponseEntity<UserDeleteResponse>(new UserDeleteResponse().id(deletedUser.getUUID()).status("USER SUCCESSFULLY DELETED"), HttpStatus.OK);

        }
        else {
            throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin"); // throw error if user is nonadmin
        }

    }

    // this method extracts the token from the JWT token string sent in the Request Header
    private String getToken(String authToken) {
        String token;
        if (authToken.startsWith("Bearer ")) {
            String [] bearerToken = authToken.split("Bearer ");
            token = bearerToken[1];
        } else {
            token = authToken;
        }
        return token;
    }
}
