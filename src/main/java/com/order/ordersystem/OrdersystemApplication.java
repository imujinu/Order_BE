package com.order.ordersystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class OrdersystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrdersystemApplication.class, args);
	}

}
