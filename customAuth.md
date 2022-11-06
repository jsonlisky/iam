自定义cas服务端授权
1.引入依赖
<dependencies>
   <!-- Custom Authentication -->
   <dependency>
       <groupId>org.apereo.cas</groupId>
       <artifactId>cas-server-core-authentication-api</artifactId>
       <version>${cas.version}</version>
   </dependency>

   <!-- Custom Configuration -->
   <dependency>
       <groupId>org.apereo.cas</groupId>
       <artifactId>cas-server-core-configuration-api</artifactId>
       <version>${cas.version}</version>
   </dependency>
</dependencies>

2.新建配置文件
在src/main/resources/META-INF新建文件

2.1 spring.factories
org.springframework.boot.autoconfigure.EnableAutoConfiguration=com.demo.cas.AuthenticationConfiguration



3.在src/main/java新建类
3.1 MyAuthenticationHandler
package com.demo.cas;


import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.ServicesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.security.auth.login.FailedLoginException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Objects;

@Component
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class MyAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {

   Logger logger = LoggerFactory.getLogger(MyAuthenticationHandler.class);

   public MyAuthenticationHandler(@Qualifier("servicesManager") ServicesManager servicesManager) {
       super(MyAuthenticationHandler.class.getSimpleName(), servicesManager, new DefaultPrincipalFactory(), 1);
   }

   @Override
   protected AuthenticationHandlerExecutionResult authenticateUsernamePasswordInternal(UsernamePasswordCredential credential, String originalPassword) throws GeneralSecurityException, PreventedException {

       final String userName = credential.getUsername();
       final String pwd = credential.getPassword();
       logger.debug(String.format("<username：%s,password:%s>", userName, pwd));
       if (validate(userName, pwd)) {
           return createHandlerResult(credential,
                   this.principalFactory.createPrincipal(userName), Collections.emptyList());
       }
       throw new FailedLoginException("Sorry, you are a failure!");
   }

   private boolean validate(String userName, String pwd) {
       return Objects.equals(userName, "dxl2") && Objects.equals(pwd, "dxl2");
   }
}

3.2 AuthenticationConfiguration
package com.demo.cas;

import com.demo.MyAuthenticationHandler;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlan;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(MyAuthenticationHandler.class)
public class AuthenticationConfiguration implements AuthenticationEventExecutionPlanConfigurer {

   @Autowired
   private MyAuthenticationHandler myAuthenticationHandler;

   /**
    * 注册验证器
    *
    * @param plan
    */
   @Override
   public void configureAuthenticationExecutionPlan(AuthenticationEventExecutionPlan plan) {
       // 注册自定义验证器注册
       plan.registerAuthenticationHandler(myAuthenticationHandler);
   }
}

4. 改造引入第三方接口
4.1 调整MyAuthenticationHandler
package com.demo.cas;

import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.ServicesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.security.auth.login.FailedLoginException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class MyAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {

   @Autowired
   private RestTemplate restTemplate;

   private Logger logger = LoggerFactory.getLogger(MyAuthenticationHandler.class);

   public MyAuthenticationHandler(@Qualifier("servicesManager") ServicesManager servicesManager) {
       super(MyAuthenticationHandler.class.getSimpleName(), servicesManager, new DefaultPrincipalFactory(), 1);
   }

   @Bean
   public RestTemplate restTemplate() {
       return new RestTemplate();
   }

   @Override
   protected AuthenticationHandlerExecutionResult authenticateUsernamePasswordInternal(UsernamePasswordCredential credential, String originalPassword) throws GeneralSecurityException, PreventedException {

       final String userName = credential.getUsername();
       final String pwd = credential.getPassword();
       logger.debug(String.format("<username：%s,password:%s>", userName, pwd));
       if (validate(userName, pwd)) {
           return createHandlerResult(credential,
                   this.principalFactory.createPrincipal(userName), Collections.emptyList());
       }
       throw new FailedLoginException("Sorry, you are a failure!");
   }

   private boolean validate(String userName, String pwd) {
       ResponseEntity<Boolean> result = restTemplate.getForEntity("http://127.0.0.1:8080/login/" + userName + "/" + pwd, Boolean.class);
       if (Objects.equals(result.getBody(), Boolean.TRUE)) {
           return true;
       } else {
           return false;
       }
   }
}

 作者：Ooo小屋 https://www.bilibili.com/read/cv19081747?spm_id_from=333.999.0.0 出处：bilibili