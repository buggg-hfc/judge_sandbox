package com.judge.judge_sandbox;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.judge.judge_sandbox.mapper")
public class JudgeSandboxApplication {

    public static void main(String[] args) {
        SpringApplication.run(JudgeSandboxApplication.class, args);
    }

}
