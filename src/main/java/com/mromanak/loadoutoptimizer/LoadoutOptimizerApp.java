package com.mromanak.loadoutoptimizer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.mromanak"})
@EnableJpaRepositories(basePackages = {"com.mromanak.loadoutoptimizer.repository"})
@EntityScan(basePackages = {"com.mromanak.loadoutoptimizer.model.jpa"})
public class LoadoutOptimizerApp {

    public static void main(String[] args) {
        SpringApplication.run(LoadoutOptimizerApp.class, args);
    }
}
