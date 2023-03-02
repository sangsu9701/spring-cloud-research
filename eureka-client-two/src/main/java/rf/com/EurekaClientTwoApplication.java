package rf.com;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import rf.com.config.ElasticsearchConfig;
import rf.com.config.SwaggerConfig;

@SpringBootApplication
@EnableEurekaClient
public class EurekaClientTwoApplication implements CommandLineRunner {

	@Autowired
	private ElasticsearchConfig config;
	@Autowired
	private SwaggerConfig config2;

	public static void main(String[] args) {
		SpringApplication.run(EurekaClientTwoApplication.class, args);
	}

	@Override
	public void run(String... strings) throws Exception {
		System.out.println(config);
		System.out.println(config2);
	}
}
