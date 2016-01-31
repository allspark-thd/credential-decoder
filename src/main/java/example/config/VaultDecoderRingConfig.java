package example.config;

import credentialdecoder.CredentialDecoder;
import credentialdecoder.vault.VaultCredentialDecoder;
import org.json.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("cloud")
public class VaultDecoderRingConfig {

    @Bean
    CredentialDecoder newDecoderRing() {

        VaultCredentialDecoder decoderRing = new VaultCredentialDecoder();

        String vcapEnv = System.getenv("VCAP_SERVICES");
        if (null != vcapEnv) {
            JSONObject vcapServices = new JSONObject(vcapEnv);
            JSONObject credentials = (JSONObject) vcapServices.get("credentials");
            decoderRing.init(credentials);
        }

        return decoderRing;
    }
}

