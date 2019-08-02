package com.riil.ws.analysis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.riil.ws.analysis.process.TsharkProcess;

@SpringBootApplication(scanBasePackages = "com.riil.ws.analysis.*")
public class ApplicationRunner {
	public static void main(String[] args) {
		ConfigurableApplicationContext app = SpringApplication.run(ApplicationRunner.class, args);
		TsharkProcess tshark = app.getBean(TsharkProcess.class);
		tshark.run();
	}
}
