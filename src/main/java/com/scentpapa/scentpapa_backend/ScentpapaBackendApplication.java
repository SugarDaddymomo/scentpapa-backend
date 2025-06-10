package com.scentpapa.scentpapa_backend;

import com.scentpapa.scentpapa_backend.models.User;
import com.scentpapa.scentpapa_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.scentpapa.scentpapa_backend.models.Role.ADMIN;

@SpringBootApplication
@RequiredArgsConstructor
public class ScentpapaBackendApplication implements CommandLineRunner {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(ScentpapaBackendApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		User user = new User();
		user.setEmail("admin@scentpapa.com");
		user.setRole(ADMIN);
		user.setPassword(passwordEncoder.encode("shiV@m69"));
		user.setFirstName("Admin");
		user.setPhoneNumber("+918527077014");

		try {
			userRepository.save(user);
		} catch (Exception e) {
			System.out.println("IGNORE ERROR");
		}
	}
}
