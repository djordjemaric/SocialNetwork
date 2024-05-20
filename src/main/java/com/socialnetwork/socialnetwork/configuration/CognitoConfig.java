package com.socialnetwork.socialnetwork.configuration;


import software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class CognitoConfig {

    @Bean
    public CognitoIdentityProviderClient cognitoIdentityProvider() {

        return CognitoIdentityProviderClient.builder()
                .credentialsProvider(AnonymousCredentialsProvider.create())
                .region(Region.EU_CENTRAL_1).build();
    }
}
