package gyarados.splitbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

/**
 * Main class.
 */
@SpringBootApplication
public class SplitBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SplitBackendApplication.class, args);
	}

	
}
