package example.config;

import credentialdecoder.vault.VaultCredentialDecoder;
import example.service.SecureDbDemo;
import org.json.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecureDbDemoConfig {

    @Bean
    SecureDbDemo secureDbDemo() {

        JSONObject credentials = new JSONObject("{}");
        VaultCredentialDecoder vaultDecoderRing = new VaultCredentialDecoder();
        try {
            String vcapEnv = System.getenv("VCAP_SERVICES");
            JSONObject vcapServices = new JSONObject(vcapEnv);
            credentials = (JSONObject) vcapServices.get("credentials");
            vaultDecoderRing = new VaultCredentialDecoder();
            vaultDecoderRing.init(credentials);
        }
        catch(Exception e) {

        }

        return new SecureDbDemo(vaultDecoderRing);
    }
}
