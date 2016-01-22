package credentialdecoder;

import org.json.JSONObject;

public interface CredentialDecoder {
    CredentialDecoder init (JSONObject props);
    String getPassword();
}
