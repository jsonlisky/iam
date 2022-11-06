package org.apereo.cas.config;

//import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.test.iam.MyCustomConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

//import org.apereo.cas.configuration.CasConfigurationProperties;

@Configuration(value = "CasOverlayOverrideConfiguration", proxyBeanMethods = false)
//@EnableConfigurationProperties(CasConfigurationProperties.class)
public class CasOverlayOverrideConfiguration {

    @Bean
    public MyCustomConfig myCustomBean() {
        MyCustomConfig myCustomConfig = new MyCustomConfig();
        myCustomConfig.setPassword("aaa");
        myCustomConfig.setUsername("lzx");

        return myCustomConfig;
    }
}
