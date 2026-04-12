package com.tttn.warehouseqr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WarehouseqrApplication {

	public static void main(String[] args) {

        System.out.println("HASH: " + new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("123456"));
        SpringApplication.run(WarehouseqrApplication.class, args);
	}
}
