package tabom.myhands;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class MyhandsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyhandsApplication.class, args);
	}

}
