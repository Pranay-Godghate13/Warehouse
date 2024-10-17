package com.ecommerce.ecommerce.security;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ecommerce.ecommerce.pojo.AppRole;
import com.ecommerce.ecommerce.pojo.Role;
import com.ecommerce.ecommerce.pojo.User;
import com.ecommerce.ecommerce.repository.RoleRepository;
import com.ecommerce.ecommerce.repository.UserRepository;
import com.ecommerce.ecommerce.security.jwt.AuthEntryPointJwt;
import com.ecommerce.ecommerce.security.jwt.AuthTokenFilter;
import com.ecommerce.ecommerce.security.services.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
// @EnableMethodSecurity
public class WebSecurityConfig {

    @Autowired
    UserDetailsServiceImpl userDetailService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    /*@Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }
            */

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        
        http.csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> 
                        auth.requestMatchers("/api/auth/**").permitAll()
                            .requestMatchers("/v3/api-docs/**").permitAll()
                            .requestMatchers("/h2-console/**").permitAll()
                       // .requestMatchers("/api/admin/**").permitAll()
                       // .requestMatchers("api/public/**").permitAll()
                            .requestMatchers("/swagger-ui/**").permitAll()
                            .requestMatchers("/api/test/**").permitAll()
                            .requestMatchers("/images/**").permitAll()
                            .anyRequest().authenticated());

        http.addFilterBefore(authenticationJwtTokenFilter(),UsernamePasswordAuthenticationFilter.class);
        http.authenticationProvider(authenticationProvider());
        http.csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**").disable());


        

        http.headers(headers -> headers.frameOptions(
                frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web -> web.ignoring().requestMatchers("/v2/api-docs",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**"));
    }

   /*  @Bean
    public CommandLineRunner initData(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder)
    {
	    return args->{
		    Role userRole=roleRepository.findByRoleName(AppRole.ROLE_USER)
			    .orElseGet(()->{
				    Role newUserRole=new Role(AppRole.ROLE_USER);
				    return roleRepository.save(newUserRole);
			    });
			
		    Role sellerRole=roleRepository.findByRoleName(AppRole.ROLE_SELLER)
			    .orElseGet(()->{
				    Role newSellerRole=new Role(AppRole.ROLE_SELLER);
				    return roleRepository.save(newSellerRole);
			    });
			
		    Role adminRole=roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
			    .orElseGet(()->{
				    Role newAdminRole=new Role(AppRole.ROLE_ADMIN);
				    return roleRepository.save(newAdminRole);
			    });
			
		    Set<Role> userRoles=Set.of(userRole);
		    Set<Role> sellerRoles=Set.of(sellerRole);
		    Set<Role> adminRoles=Set.of(userRole,sellerRole,adminRole);
		
		    if(!userRepository.existsByUsername("user1"))
		    {
			    User user1=new User("user1","user1@example.com",passwordEncoder.encode("password1"));
			    userRepository.save(user1);
		    }
		
		    if(!userRepository.existsByUsername("seller1"))
		    {
			    User seller1=new User("seller1","seller1@example.com",passwordEncoder.encode("password2"));
			    userRepository.save(seller1);
		    }
		
		    if(!userRepository.existsByUsername("admin"))
		    {
			    User admin=new User("admin","admin@example.com",passwordEncoder.encode("adminPass"));
			    userRepository.save(admin);
		    }
		
		    userRepository.findByUsername("user1").ifPresent(user->{
			    user.setRoles(userRoles);
			    userRepository.save(user);
		});
		    userRepository.findByUsername("seller1").ifPresent(user->{
			    user.setRoles(sellerRoles);
			    userRepository.save(user);
		});
		    userRepository.findByUsername("admin").ifPresent(user->{
			    user.setRoles(adminRoles);
			    userRepository.save(user);
		});
	};*/
    @Bean
public CommandLineRunner initData(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
    return args -> {
        Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
            .orElseGet(() -> {
                Role newUserRole = new Role(AppRole.ROLE_USER);
                return roleRepository.save(newUserRole);
            });

        Role sellerRole = roleRepository.findByRoleName(AppRole.ROLE_SELLER)
            .orElseGet(() -> {
                Role newSellerRole = new Role(AppRole.ROLE_SELLER);
                return roleRepository.save(newSellerRole);
            });

        Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
            .orElseGet(() -> {
                Role newAdminRole = new Role(AppRole.ROLE_ADMIN);
                return roleRepository.save(newAdminRole);
            });

        Set<Role> userRoles = Set.of(userRole);
        Set<Role> sellerRoles = Set.of(sellerRole);
        Set<Role> adminRoles = Set.of(userRole, sellerRole, adminRole);

        if (!userRepository.existsByUsername("user1")) {
            User user1 = new User("user1", "user1@example.com", passwordEncoder.encode("password1"));
            user1.setRoles(userRoles);
            userRepository.save(user1);
        }

        if (!userRepository.existsByUsername("seller1")) {
            User seller1 = new User("seller1", "seller1@example.com", passwordEncoder.encode("password2"));
            seller1.setRoles(sellerRoles);
            userRepository.save(seller1);
        }

        if (!userRepository.existsByUsername("admin")) {
            User admin = new User("admin", "admin@example.com", passwordEncoder.encode("adminPass"));
            admin.setRoles(adminRoles);
            userRepository.save(admin);
        }
    };
}

}

