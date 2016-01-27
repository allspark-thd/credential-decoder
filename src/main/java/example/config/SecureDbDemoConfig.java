package dbBrokerClient.config;

import example.service.SecureDbDemo;
import org.json.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecureDbDemoConfig {

    @Bean
    SecureDbDemo secureDbDemo() {

        JSONObject credentials = new JSONObject("{}");
        try
        {
            String vcapEnv = System.getenv("VCAP_SERVICES");
            JSONObject vcapServices = new JSONObject(vcapEnv);
            credentials = (JSONObject) vcapServices.get("credentials");
        }
        catch(Exception e)
        {

        }

        return new SecureDbDemo(credentials);
    }
}
