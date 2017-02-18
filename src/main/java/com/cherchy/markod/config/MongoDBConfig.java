package com.cherchy.markod.config;

import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import java.net.UnknownHostException;

@Configuration
public class MongoDBConfig {

    @Autowired
    private Environment env;

    @Bean
    public MongoDbFactory mongoDbFactory() throws UnknownHostException {
        return new SimpleMongoDbFactory(new MongoClient(), env.getProperty("spring.data.mongodb.database"));
    }

    @Bean
    public MongoTemplate mongoTemplate() throws UnknownHostException {
        //remove _class
        /*MappingMongoConverter converter =
                new MappingMongoConverter(mongoDbFactory(), new MongoMappingContext());
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));

        CustomConversions customConversions = new CustomConversions(GeoJsonConverters.getConvertersToRegister());
        converter.setCustomConversions(customConversions);
        customConversions.registerConvertersIn((GenericConversionService) converter.getConversionService());

        return new MongoTemplate(mongoDbFactory(), converter);
        */
        return new MongoTemplate(mongoDbFactory());
    }

    @Bean
    public GridFsTemplate gridFsTemplate() throws Exception {
        return new GridFsTemplate(mongoDbFactory(), new MappingMongoConverter(mongoDbFactory(), new MongoMappingContext()));
    }
}