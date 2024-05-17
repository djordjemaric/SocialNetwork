package com.socialnetwork.socialnetwork.service;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import com.socialnetwork.socialnetwork.dto.LoginResponse;
import com.socialnetwork.socialnetwork.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class CognitoService {
    @Value("${COGNITO_CLIENT_ID}")
    private String clientId;

    private final AWSCognitoIdentityProvider cognitoIdentityProvider;

    @Autowired
    public CognitoService(AWSCognitoIdentityProvider cognitoIdentityProvider) {
        this.cognitoIdentityProvider = cognitoIdentityProvider;
    }

    public boolean registerUser(String username, String email, String password) {
        // Set up the AWS Cognito registration request
        SignUpRequest signUpRequest = new SignUpRequest()
                .withClientId(clientId)
                .withUsername(username)
                .withPassword(password)
                .withUserAttributes(
                        new AttributeType().withName("email").withValue(email)
                );

        // Register the user with Amazon Cognito
        try {
            SignUpResult signUpResponse = cognitoIdentityProvider.signUp(signUpRequest);
            System.out.println(signUpResponse.toString());
            return true;

        } catch (Exception e) {
            throw new RuntimeException("User registration failed: " + e.getMessage(), e);
        }
    }

    public Optional<LoginResponse> loginUser(String email, String password) {
        // Set up the authentication request
        InitiateAuthRequest authRequest = new InitiateAuthRequest()
                .withAuthFlow("USER_PASSWORD_AUTH")
                .withClientId(clientId)
                .withAuthParameters(
                        Map.of(
                                "USERNAME", email,
                                "PASSWORD", password
                        )
                );

        try {
            InitiateAuthResult authResult = cognitoIdentityProvider.initiateAuth(authRequest);
            AuthenticationResultType authResponse = authResult.getAuthenticationResult();

            // At this point, the user is successfully authenticated, and you can access JWT tokens:
            String accessToken = authResponse.getAccessToken();
            String refreshToken = authResponse.getRefreshToken();
            Integer expiresIn = authResponse.getExpiresIn();

            LoginResponse loginResponse = new LoginResponse(accessToken, refreshToken, expiresIn);
            Optional<LoginResponse> loginResponseOpt = Optional.of(loginResponse);
            return loginResponseOpt;
            //TODO: integrate with security? You can decode and verify the JWT tokens for user information


            // do we need this?
//            User loggedInUser = new User();
//            loggedInUser.setAccessToken(accessToken);
//
//            return loggedInUser;

        } catch (Exception e) {
            Optional<LoginResponse> loginResponseOpt = Optional.empty();
            return loginResponseOpt;
//            throw new RuntimeException("User login failed: " + e.getMessage(), e);
        }
    }

}
