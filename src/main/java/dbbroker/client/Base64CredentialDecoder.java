package dbbroker.client;

import org.json.JSONObject;
import java.util.Base64;

public class Base64CredentialDecoder implements CredentialDecoder {

    private String password = null;

    @Override
    public CredentialDecoder init(JSONObject props) {
        this.password = decode(extractPassword(props));
        return this;
    }

    private static String decode(String base64str) {
        try {
            return new String(Base64.getDecoder().decode(base64str));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("`credentials.password` must be base64 encoded");
        }
    }

    private static String extractPassword(JSONObject props) {
        try {
            return props.getJSONObject("credentials").getString("password");
        } catch (Exception e) {
            throw new RuntimeException("`credentials.password` not found");
        }
    }

    @Override
    public String getPassword() {
        return password;
    }
}
