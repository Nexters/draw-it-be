package com.draw.it.api.global.config

import com.draw.it.api.external.notification.client.BizNotificationClient
import com.draw.it.api.global.filter.ExceptionHandleFilter
import com.draw.it.api.global.filter.TraceFilter
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.Filter
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.util.*

@Configuration
class WebMvcConfig(
): WebMvcConfigurer {
    
    @Bean
    fun timeZone(): TimeZone {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"))
        return TimeZone.getDefault()
    }
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("*")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(false)
            .maxAge(3600)
    }

    @Bean
    fun addTraceFilter(): FilterRegistrationBean<Filter> {
        val filterRegistration = FilterRegistrationBean<Filter>()
        filterRegistration.filter = TraceFilter()
        filterRegistration.order = Integer.MIN_VALUE
        filterRegistration.addUrlPatterns("/*")
        return filterRegistration
    }

    @Bean
    fun addExceptionHandleFilter(objectMapper: ObjectMapper, bizNotificationClient: BizNotificationClient): FilterRegistrationBean<Filter> {
        val filterRegistration = FilterRegistrationBean<Filter>()
        filterRegistration.filter = ExceptionHandleFilter(objectMapper, bizNotificationClient)
        filterRegistration.order = Integer.MIN_VALUE + 1
        filterRegistration.addUrlPatterns("/*")
        return filterRegistration
    }
}
