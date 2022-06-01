package com.documentDemo.documentDemo;

import com.documentDemo.documentDemo.config.FileStorageProps;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

@SpringBootApplication
@EnableConfigurationProperties({
		FileStorageProps.FileStorageProperties.class
})
public class DocumentDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocumentDemoApplication.class, args);
	}

}
