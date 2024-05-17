package com.socialnetwork.socialnetwork.configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.s3.model.Region;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CognitoConfig {

    @Bean
    public AWSCognitoIdentityProvider cognitoIdentityProvider() {

        return AWSCognitoIdentityProviderClientBuilder.standard()
                .withRegion(String.valueOf(Region.EU_Frankfurt))
                .build();
    }
}

