package com.example.Eshop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/images/**")
        .addResourceLocations("file:///c:/images/");
  }

  @Override
  public void configureContentNegotiation(
      ContentNegotiationConfigurer configurer) {
    configurer.defaultContentType(MediaType.APPLICATION_OCTET_STREAM);
    configurer.mediaType("svg", MediaType.valueOf("image/svg+xml"))
        .mediaType("png", MediaType.IMAGE_PNG)
        .mediaType("jpg", MediaType.IMAGE_JPEG)
        .mediaType("jpeg", MediaType.IMAGE_JPEG)
        .mediaType("gif", MediaType.IMAGE_GIF);
  }
}