package com.draw.it.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = ["com.draw.it.api"])
@EnableScheduling
class DrawItApiApplication

fun main(args: Array<String>) {
	runApplication<DrawItApiApplication>(*args)
}
