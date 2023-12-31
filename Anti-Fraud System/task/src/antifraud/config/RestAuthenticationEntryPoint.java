package antifraud.config;

import antifraud.service.UserDetailsServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Basic")) {
            // Extract the Base64 encoded part of the header
            String base64Credentials = authHeader.substring("Basic".length()).trim();

            // Decode the Base64 encoded string
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);

            // credentials = username:password
            final String[] values = credentials.split(":",2);

            // Now you have username in values[0]
            UserDetailsServiceImpl.saveCurrentUser("Unauthorized: " + authException.getMessage()
                    + "\n\t| name: " + values[0] + ", password: " + values[1]
                    + "\n\t| uri " + request.getServletPath() + ", method: " + request.getMethod() + ", " + request.getContextPath());
        }

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
    }

}
