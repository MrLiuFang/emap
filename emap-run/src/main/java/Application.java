
import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.pepper.core.BaseDaoFactoryBean;
import com.pepper.core.dubbo.DubboDynamicVersion;

@DubboDynamicVersion
@SpringBootApplication(scanBasePackages = { "com.pepper.controller.**", "com.pepper.service.**", "com.pepper.util.**",
		"com.pepper.core.**", "com.pepper.model.**", "com.pepper.init.data.console.**","com.pepper.register.**"})
@EnableJpaRepositories(basePackages = "com.pepper.dao.**", repositoryFactoryBeanClass = BaseDaoFactoryBean.class)
@EntityScan("com.pepper.model.**")
@PropertySource(value = { "classpath:emap-run.properties"}, ignoreResourceNotFound = true, encoding = "UTF-8")
@DubboComponentScan(basePackages = { "com.pepper.controller.**", "com.pepper.service.**", "com.pepper.util.**",
		"com.pepper.core.**", "com.pepper.init.data.console.**" })
@EnableScheduling
public class Application extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}

	public static void main(String args[]) throws Exception {
		/*
		 * new SpringApplicationBuilder(Application.class)
		 * .web(WebApplicationType.NONE) .run(args);
		 */
		SpringApplication.run(Application.class, args);
	}
	
	@Bean
	public FilterRegistrationBean<BodyReaderFilter> Filters() {
	    FilterRegistrationBean<BodyReaderFilter> registrationBean = new FilterRegistrationBean<BodyReaderFilter>();
	    registrationBean.setFilter(new BodyReaderFilter());
	    registrationBean.addUrlPatterns("/*");
	    registrationBean.setName("koalaSignFilter");
	    return registrationBean;
	}
}