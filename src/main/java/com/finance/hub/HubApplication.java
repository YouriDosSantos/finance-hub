package com.finance.hub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class HubApplication{

//	implements CommandLineRunner  this would go on the above

//	@Autowired
//	private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(HubApplication.class, args);
	}

//	@Override
//	public void run(String... args) throws Exception {
//		System.out.println("ENCODE = " + passwordEncoder.encode("123456"));
//	}
}
