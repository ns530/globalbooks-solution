import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class OAuth2SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/orders/**").authenticated()
                .and()
            .oauth2ResourceServer()
                .jwt();
    }
}

// Note: Add to OrdersService pom.xml:
// <dependency>
//     <groupId>org.springframework.security</groupId>
//     <artifactId>spring-security-oauth2-resource-server</artifactId>
// </dependency>
// <dependency>
//     <groupId>org.springframework.security</groupId>
//     <artifactId>spring-security-oauth2-jose</artifactId>
// </dependency>

// Configure application.yml with:
// spring:
//   security:
//     oauth2:
//       resourceserver:
//         jwt:
//           issuer-uri: http://localhost:8080/auth/realms/globalbooks