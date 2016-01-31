package example.config;

import credentialdecoder.CredentialDecoder;
import credentialdecoder.base64.Base64CredentialDecoder;
import org.json.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Base64DecoderRingConfig {

    @Bean
    CredentialDecoder newDecoderRing() {

        Base64CredentialDecoder decoderRing = new Base64CredentialDecoder();

        String vcapEnv = System.getenv("VCAP_SERVICES");
        if (null != vcapEnv) {
            JSONObject vcapServices = new JSONObject(vcapEnv);
            JSONObject credentials = (JSONObject) vcapServices.get("credentials");
            decoderRing.init(credentials);
        }

        return decoderRing;
    }
}

