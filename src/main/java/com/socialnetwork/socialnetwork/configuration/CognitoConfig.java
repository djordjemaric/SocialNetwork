package com.socialnetwork.socialnetwork.configuration;


import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class CognitoConfig {

    @Bean
    public CognitoIdentityProviderClient cognitoIdentityProvider() {

        return CognitoIdentityProviderClient.builder()
                .region(Region.EU_CENTRAL_1).build();
    }
}

