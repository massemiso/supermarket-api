package com.massemiso.supermarket_api.config;

import com.massemiso.supermarket_api.service.UserService;
import com.massemiso.supermarket_api.util.JwtUtil;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Customizer to dynamically inject test JWT tokens into the Swagger UI description.
 * This allows developers to easily test the API with different roles without manually generating tokens.
 *
 * This implementation reuses the existing security infrastructure:
 * 1. It identifies the usernames from mock-users.properties.
 * 2. It loads the UserDetails (and authorities) using the existing UserService.
 * 3. It generates a valid JWT token using the production-ready JwtUtil.
 *
 * This feature is only enabled in non-production environments (!prod).
 */
@Component
@Profile("!prod")
@PropertySource("classpath:mock-users.properties")
public class SwaggerTokenDescriptionCustomizer implements OpenApiCustomizer {

  private final UserService userService;
  private final JwtUtil jwtUtil;

  // We retrieve the usernames from the same source as UserSeeder
  @Value("${user.admin.username}") private String adminUsername;
  @Value("${user.manager.username}") private String managerUsername;
  @Value("${user.cashier.username}") private String cashierUsername;
  @Value("${user.guest.username}") private String guestUsername;

  public SwaggerTokenDescriptionCustomizer(UserService userService, JwtUtil jwtUtil) {
    this.userService = userService;
    this.jwtUtil = jwtUtil;
  }

  /**
   * Generates a token for a specific user and appends it to the description builder.
   * To add more roles in the future:
   * 1. Define the role and user in mock-users.properties.
   * 2. Add an @Value for the username here.
   * 3. Call this method with the new role label and username.
   */
  private void appendToken(StringBuilder sb, String roleLabel, String username) {
    try {
      // Load user details to get real authorities
      UserDetails userDetails = userService.loadUserByUsername(username);

      // Construct Authentication object for JwtUtil
      Authentication auth = new UsernamePasswordAuthenticationToken(
          userDetails.getUsername(),
          null,
          userDetails.getAuthorities()
      );

      // Use existing logic to sign a new token
      String token = jwtUtil.createToken(auth);

      sb.append("**").append(roleLabel).append("**:\n")
          .append("`").append(token).append("`\n\n");

    } catch (Exception e) {
      // Fallback if users haven't been seeded yet or user doesn't exist
      sb.append("**").append(roleLabel)
          .append("**: (Token generation pending - Ensure mock users are seeded)\n\n");
    }
  }

  /**
   * Customizes the OpenAPI definition by appending a "Test Tokens" section to the description.
   * @param openApi The OpenAPI instance to customize.
   */
  @Override
  public void customise(OpenAPI openApi) {
    Info info = openApi.getInfo();
    String currentDescription = info.getDescription();

    // Avoid double-appending if customize is called multiple times
    if (currentDescription != null && currentDescription.contains("## Test Tokens")) {
      return;
    }

    StringBuilder tokenDescription = new StringBuilder(currentDescription == null ? "" : currentDescription);
    tokenDescription.append("\n\n---\n\n## Test Tokens\n\n")
        .append("Use these pre-generated tokens to authorize requests in Swagger UI. ")
        .append("Copy the entire line and paste it into the **Authorize** dialog.\n\n");

    // Generate and append tokens for each role
    appendToken(tokenDescription, "ADMIN", adminUsername);
    appendToken(tokenDescription, "MANAGER", managerUsername);
    appendToken(tokenDescription, "CASHIER", cashierUsername);
    appendToken(tokenDescription, "GUEST", guestUsername);

    info.setDescription(tokenDescription.toString());
  }
}
