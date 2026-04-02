package com.haizhou.smarttax;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class SmartTaxApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartTaxApplication.class, args);
        System.out.println("===========================================");
        System.out.println("    税税通后端服务启动成功！");
        System.out.println("    访问地址: cd D:\\website\\phone3\\phone\\backend");
        System.out.println("===========================================");
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
