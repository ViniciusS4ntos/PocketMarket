package com.pocketmarket.user.dtos.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMappermodelMapper() {
        return new ModelMapper();
    }

}
