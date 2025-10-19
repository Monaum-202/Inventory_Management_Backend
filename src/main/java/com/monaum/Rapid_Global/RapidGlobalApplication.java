package com.monaum.Rapid_Global;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class RapidGlobalApplication {

    public static void main(String[] args) {
        SpringApplication.run(RapidGlobalApplication .class, args);
    }

}
