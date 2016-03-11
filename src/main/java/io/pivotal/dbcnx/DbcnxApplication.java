package io.pivotal.dbcnx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DbcnxApplication {
	static {
		System.setProperty("java.naming.factory.url.pkgs", "org.apache.naming");
		
	}
	public static void main(String[] args) {
		SpringApplication.run(DbcnxApplication.class, args);
	}
}
