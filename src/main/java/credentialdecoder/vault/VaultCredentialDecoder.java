package credentialdecoder.vault;


import org.apache.commons.codec.Charsets;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.function.Function;

public class VaultCredentialDecoder implements credentialdecoder.CredentialDecoder {

    private String app, user, vault_url, login_path, creds_path;
    static HttpClient client = HttpClients.createDefault();

    public VaultCredentialDecoder(JSONObject props) {
        init(props);
    }

    void init(JSONObject props) {
        this.app = getApp.apply(props);
        this.user = getUser.apply(props);
        this.vault_url = getVaultUrl.apply(props);
        this.creds_path = vault_url + getCredsPath.apply(props);
        this.login_path = vault_url + getLoginPath.apply(props);
    }

    private static Function<JSONObject, String> getField(String field) {
        return (JSONObject json) -> {
            try {
                return json.getJSONObject("credentials").getString(field);
            } catch (Exception e) {
                throw new RuntimeException(
                        String.format("`credentials.%s` not found. `app_id`, `user_id`, `vault_url`, `login_path`, and `creds_path` are required", field)
                );
            }
        };
    }

    static Function<JSONObject, String>
            getApp = getField("app_id"),
            getUser = getField("user_id"),
            getVaultUrl = getField("vault_url"),
            getCredsPath = getField("creds_path"),
            getLoginPath = getField("login_path");


    static JSONObject handle(HttpResponse response) {
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new VaultException(response);
        }
        try {
            return new JSONObject(
                    EntityUtils.toString(response.getEntity(), Charsets.UTF_8)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static JSONObject send(HttpUriRequest request) {
        try {
            return client
                    .execute(
                            request,
                            VaultCredentialDecoder::handle
                    );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static HttpEntity toEntity(JSONObject json) {
        return new StringEntity(json.toString(), Charsets.UTF_8);
    }

    HttpUriRequest credsRequest(String token) {
        return RequestBuilder
                .get(creds_path)
                .addHeader("X-Vault-Token", token)
                .build();
    }

    static Function<JSONObject, JSONObject> getjson(String field) {
        return json -> json.getJSONObject(field);
    }

    static Function<JSONObject, String> getjsonstr(String field) {
        return json -> json.getString(field);
    }

    String extractToken(JSONObject json) {
        return json.getJSONObject("auth").getString("client_token");
    }

    HttpUriRequest tokenRequest(JSONObject credentials) {
        return RequestBuilder
                .post(login_path)
                .setEntity(
                        toEntity(credentials))
                .build();
    }

    JSONObject fetchToken(JSONObject credentials) {
        return send(tokenRequest(credentials));
    }

    JSONObject fetchCreds(String token) {
        return send(credsRequest(token));
    }

    String getToken(JSONObject appCreds) {
        return extractToken(
                fetchToken(appCreds));
    }

    JSONObject getVaultData() {
        return fetchCreds(
                getToken(getAppCreds())
        )
                .getJSONObject("data");
    }

    JSONObject getAppCreds() {
        return new JSONObject()
                .put("app_id", app)
                .put("user_id", user);
    }

    @Override
    public String getPassword() {
        return getVaultData()
                .getString("value");
    }
}

class VaultException extends RuntimeException {
    public VaultException(HttpResponse response) {
        super(response.getStatusLine().getStatusCode() +
                " : " +
                response.getStatusLine().getReasonPhrase());
    }
}
