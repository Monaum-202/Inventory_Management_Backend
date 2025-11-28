package com.monaum.Rapid_Global;

import com.monaum.Rapid_Global.config.DataAuditorAware;
import com.monaum.Rapid_Global.module.personnel.user.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@EnableAsync
@EnableScheduling
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class RapidGlobalApplication {

    public static void main(String[] args) {
        SpringApplication.run(RapidGlobalApplication .class, args);
    }

//    @Bean
//    AuditorAware<User> auditorAware() {
//        return new DataAuditorAware();
//    }

//    @Bean
//    PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
}
