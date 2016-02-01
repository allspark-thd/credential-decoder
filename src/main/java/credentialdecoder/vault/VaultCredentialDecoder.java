package credentialdecoder.vault;


import org.apache.commons.codec.Charsets;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
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

    @Override
    public credentialdecoder.CredentialDecoder init(JSONObject props) {
        this.app = getApp.apply(props);
        this.user = getUser.apply(props);
        this.vault_url = getVaultUrl.apply(props);
        this.creds_path = vault_url + getCredsPath.apply(props);
        this.login_path = vault_url + getLoginPath.apply(props);
        return this;
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


    static RuntimeException responseErr(HttpResponse response) {
        return new RuntimeException(
                response.getStatusLine().getStatusCode() +
                        " : " +
                        response.getStatusLine().getReasonPhrase()
        );
    }

    static ResponseHandler<JSONObject> toJson =
            response -> {
                if (response.getStatusLine().getStatusCode() != 200) {
                    throw responseErr(response);
                }
                try {
                    return new JSONObject(
                            EntityUtils.toString(response.getEntity(), Charsets.UTF_8)
                    );
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            };

    static Function<HttpUriRequest, JSONObject> send =
            request -> {
                try {
                    return client
                            .execute(
                                    request,
                                    toJson
                            );
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            };

    static Function<JSONObject, HttpEntity> toEntity =
            json -> new StringEntity(json.toString(), Charsets.UTF_8);


    static Function<RequestBuilder, HttpUriRequest> buildRequest =
            RequestBuilder::build;

    Function<String, HttpUriRequest> credsRequest =
            token -> RequestBuilder
                    .get(creds_path)
                    .addHeader("X-Vault-Token", token).build();

    static Function<String, Function<JSONObject, JSONObject>>
            getjson = field -> json -> json.getJSONObject(field);

    static Function<String, Function<JSONObject, String>> getjsonstr =
            field -> json -> json.getString(field);

    static Function<JSONObject, String> extractToken =
            getjsonstr
                    .apply("client_token")
                    .compose(getjson.apply("auth"));

    Function<JSONObject, HttpUriRequest> tokenRequest =
            buildRequest
                    .compose(
                            RequestBuilder
                                    .post(login_path)
                                    ::setEntity
                    )
                    .compose(toEntity);
    Function<JSONObject, JSONObject>
            fetchToken = send.compose(tokenRequest);
    Function<String, JSONObject>
            fetchCreds = send.compose(credsRequest);

    String getToken(JSONObject appCreds) {
        return extractToken
                .compose(fetchToken)
                .apply(appCreds);
    }

    public JSONObject getCreds(JSONObject json) {
        return
                getjson
                        .apply("data")
                        .compose(fetchCreds)
                        .compose(this::getToken)
                        .apply(json);
    }

    @Override
    public String getPassword() {
        return getCreds(new JSONObject().put("app_id", app).put("user_id", user))
                .toString()
                ;
    }
}
