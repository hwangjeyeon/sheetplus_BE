package sheetplus.checkings;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;


@EnableRetry
@EnableJpaAuditing
@SpringBootApplication
public class CheckingApplication {

	public static void main(String[] args) {
		SpringApplication.run(CheckingApplication.class, args);
	}

}
