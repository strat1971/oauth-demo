package com.cafegalactica.oauth_demo;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebController {

    @GetMapping("/")
    public String publicPage() {
        return "<h1>Welcome</h1><p>This is the public page.</p><p><a href='/protected'>Go to protected page</a></p>";
    }

    @GetMapping("/protected")
    public String protectedPage(@AuthenticationPrincipal OAuth2User principal) {
        // The 'principal' object contains the user's information from Google
        var name = principal.getAttribute("name");
        var email = principal.getAttribute("email");

        return "<h1>Welcome, " + name + "!</h1>" +
            "<p>You are logged in with email: " + email + "</p>";
    }
}
