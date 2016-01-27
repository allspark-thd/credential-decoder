package example.service.util;

import org.json.JSONObject;

public class VaultHelper {

    private String url;
    private String user;
    private String password;

    public VaultHelper(JSONObject secureDbProps) {
        String token = getToken(secureDbProps);
        setVaultData(token);
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getToken(JSONObject secureDbProps) {
        return "token";
    }

    public void setVaultData(String token) {
        this.url = "www.google.com";
        this.user = "user";
        this.password = "password";
    }
}
