package de.serdioa.ignite.server;

import java.util.Map;
import java.util.TreeMap;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class Application {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(Application.class)
                .web(WebApplicationType.NONE)
                .build()
                .run(args);
        
        Map<String, Object> beans = new TreeMap<>(context.getBeansOfType(Object.class));
        for (Map.Entry<String, Object> bean : beans.entrySet()) {
            System.out.println(bean.getKey() + " -> " + bean.getValue().getClass());
            
            if (Map.class.isAssignableFrom(bean.getClass())) {
                Map<?, ?> map = (Map<?, ?>) bean;
                if (!map.isEmpty()) {
                    Object key = map.keySet().iterator().next();
                    Object value = map.get(key);
                    
                    if (value instanceof org.apache.ignite.configuration.CacheConfiguration<?, ?>) {
                        System.out.println("    -> Ignite Cache Configuration");
                    }
                }
            }
        }
    }
}
