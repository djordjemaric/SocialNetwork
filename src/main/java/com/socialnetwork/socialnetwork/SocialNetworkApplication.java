package com.socialnetwork.socialnetwork;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SocialNetworkApplication {

	@ResponseStatus(HttpStatus.OK)
    @GetMapping("/hello")
    public String hello() {
        return "Hello from demo! Pipeline is working good.";
    }
	public static void main(String[] args) {
		SpringApplication.run(SocialNetworkApplication.class, args);
	}
}

