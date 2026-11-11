package tn.esprit.pharmap.drugcatalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class DrugCatalogServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DrugCatalogServiceApplication.class, args);
	}

}
