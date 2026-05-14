package com.massemiso.supermarket_api.config.seeder;


import com.massemiso.supermarket_api.service.UserService;
import com.massemiso.supermarket_api.util.JwtUtil;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@Profile("!prod")
@PropertySource("classpath:mock-users.properties")
@Slf4j
@Order(3)
public class SwaggerTokenDescriptionCustomizer implements OpenApiCustomizer {

  private final UserService userService;
  private final JwtUtil jwtUtil;

  private volatile String cachedTokenDescription = "";

  @Value("${user.admin.username}") private String adminUsername;
  @Value("${user.manager.username}") private String managerUsername;
  @Value("${user.cashier.username}") private String cashierUsername;
  @Value("${user.guest.username}") private String guestUsername;

  @Autowired
  public SwaggerTokenDescriptionCustomizer
      (UserService userService, JwtUtil jwtUtil) {
    this.userService = userService;
    this.jwtUtil = jwtUtil;
  }

  @EventListener(ApplicationReadyEvent.class)
  public void onApplicationReady() {
    try {
      log.info("SWAGGER: Application ready. Generating initial tokens...");
      refreshTokens();
    } catch (InterruptedException e) {
      log.error("Failed to generate initial Swagger tokens", e);
    }
  }

  /**
   * This method runs every 30 minutes (1800000 ms).
   * It recalculates the JWTs using JwtUtil.
   */
  @Scheduled(fixedRate = 1_800_000, initialDelay = 1_799_999)
  public void refreshTokens() throws InterruptedException {
    if (adminUsername == null) return; // Guard clause
    log.info("SWAGGER: Refreshing pre-generated test tokens...");
    StringBuilder sb = new StringBuilder();
    sb.append("\n\n---\n\n## Test Tokens (Refreshed every 30m)\n\n")
        .append("Use these tokens to authorize requests. ")
        .append("Copy the entire line and paste it into the **Authorize** dialog. ")
        .append("If a token expires, refresh the page.\n\n");

    appendToken(sb, "ADMIN", adminUsername);
    appendToken(sb, "MANAGER", managerUsername);
    appendToken(sb, "CASHIER", cashierUsername);
    appendToken(sb, "GUEST", guestUsername);

    this.cachedTokenDescription = sb.toString();
    log.info("SWAGGER: Test tokens generated!");
  }

  private void appendToken
      (StringBuilder sb, String roleLabel, String username) {
    try {
      UserDetails userDetails = userService.loadUserByUsername(username);
      Authentication auth = new UsernamePasswordAuthenticationToken(
          userDetails.getUsername(), null, userDetails.getAuthorities());

      String token = jwtUtil.createToken(auth);
      sb.append("**").append(roleLabel).append("**: `").append(token).append("`\n\n");
    } catch (Exception e) {
      sb.append("**").append(roleLabel).append("**: (Generation pending)\n\n");
    }
  }

  @Override
  public void customise(OpenAPI openApi) {
    Info info = openApi.getInfo();
    String baseDescription = "API Documentation for a Supermarket RESTful monolith app."
    + " In non-production environments, valid test tokens are automatically appended"
     + " to this description.";
    info.setDescription(baseDescription + cachedTokenDescription);
  }
}