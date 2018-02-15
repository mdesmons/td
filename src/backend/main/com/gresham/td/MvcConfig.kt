package com.gresham.td

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import org.springframework.web.servlet.resource.PathResourceResolver
import org.springframework.web.filter.ShallowEtagHeaderFilter
import org.springframework.context.annotation.Bean
import javax.servlet.Filter


class MvcConfig : WebMvcConfigurerAdapter(){
	override fun addResourceHandlers(registry: ResourceHandlerRegistry?) {
		registry!!
				.addResourceHandler("/**")
				.addResourceLocations("/resources/","/resources/static/","/resources/static/built/")
				.setCachePeriod(3600)
				.resourceChain(true)
				.addResolver(PathResourceResolver());	}


}
