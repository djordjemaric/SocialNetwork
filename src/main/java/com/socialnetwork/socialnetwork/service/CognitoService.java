package com.socialnetwork.socialnetwork.service;


import com.socialnetwork.socialnetwork.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;
;


import java.util.Map;

@Service
public class CognitoService {
    @Value("${aws.cognito.client-id}")
    private String clientId;

    private final CognitoIdentityProviderClient cognitoIdentityProvider;
    
    public CognitoService(CognitoIdentityProviderClient cognitoIdentityProvider) {
        this.cognitoIdentityProvider = cognitoIdentityProvider;
    }

    public void registerUser(String username, String email, String password) {
        // Set up the AWS Cognito registration request
        SignUpRequest signUpRequest = SignUpRequest.builder().
                clientId(clientId)
                .username(username)
                .password(password)
                .userAttributes(
                        AttributeType.builder().name("email").value(email).build()
                ).build();

        // Register the user with Amazon Cognito
        try {
            cognitoIdentityProvider.signUp(signUpRequest);

        } catch (Exception e) {
            throw new RuntimeException("User registration failed: " + e.getMessage(), e);
        }
    }

    public LoginResponse loginUser(String email, String password) {
        // Set up the authentication request
        InitiateAuthRequest authRequest = InitiateAuthRequest.builder()
                .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                .clientId(clientId)
                .authParameters(
                        Map.of(
                                "USERNAME", email,
                                "PASSWORD", password
                        )
                ).build();

        try {
            InitiateAuthResponse authResult = cognitoIdentityProvider.initiateAuth(authRequest);
            AuthenticationResultType authResponse = authResult.authenticationResult();

            // At this point, the user is successfully authenticated, and you can access JWT tokens:
            String accessToken = authResponse.accessToken();
            String refreshToken = authResponse.refreshToken();
            Integer expiresIn = authResponse.expiresIn();

            return new LoginResponse(accessToken, refreshToken, expiresIn);

        } catch (Exception e) {
            throw new RuntimeException("User login failed: " + e.getMessage(), e);
        }
    }

}
