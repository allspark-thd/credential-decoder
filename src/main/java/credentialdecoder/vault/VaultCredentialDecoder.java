package credentialdecoder.vault;


import credentialdecoder.CredentialDecoder;
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

public class VaultCredentialDecoder implements CredentialDecoder {

    private String app, user;
    static HttpClient client = HttpClients.createDefault();

    @Override
    public CredentialDecoder init(JSONObject props) {
        this.app = getApp.apply(props);
        this.user = getUser.apply(props);

        return this;
    }

    private static Function<JSONObject, String> getField(String field) {
        return (JSONObject json) -> {
            try {
                return json.getJSONObject("credentials").getString(field);
            } catch (Exception e) {
                throw new RuntimeException(
                        String.format("`credentials.%s` not found. `app_id` and `user_id` are required.", field)
                );
            }
        };
    }

    static Function<JSONObject, String>
            getApp = getField("app_id"),
            getUser = getField("user_id");

    static String VAULT = "http://172.24.100.33:8200";
    static String CREDS_URL = VAULT + "/v1/secret/dbidtest";
    static String LOGIN_URL = "/v1/auth/app-id/login";

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

    static Function<String, HttpUriRequest> credsRequest =
            token -> RequestBuilder
                    .get(CREDS_URL)
                    .addHeader("X-Vault-Token", token).build();

    static Function<String, Function<JSONObject, JSONObject>>
            getjson = field -> json -> json.getJSONObject(field);

    static Function<String, Function<JSONObject, String>> getjsonstr =
            field -> json -> json.getString(field);

    static Function<JSONObject, String> extractToken =
            getjsonstr
                    .apply("client_token")
                    .compose(getjson.apply("auth"));

    static Function<JSONObject, HttpUriRequest> tokenRequest =
            buildRequest
                    .compose(
                            RequestBuilder
                                    .post(LOGIN_URL)
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
