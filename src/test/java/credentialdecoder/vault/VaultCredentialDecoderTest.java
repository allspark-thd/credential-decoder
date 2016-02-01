package credentialdecoder.vault;

import org.json.JSONObject;
import org.junit.Test;
import org.mockito.Mockito;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class VaultCredentialDecoderTest {
    @Test
    public void should_throw_meaningful_message_when_null() {
        expect_init_error_message(
                null,
                "`app_id`, `user_id`, `vault_url`, `login_path`, and `creds_path` are required"
        );
    }

    @Test
    public void should_throw_meaningful_message_when_no_json() {
        expect_init_error_message(
                new JSONObject(),
                "`app_id`, `user_id`, `vault_url`, `login_path`, and `creds_path` are required"
        );
    }

    @Test
    public void should_throw_meaningful_message_when_no_application() {
        expect_init_error_message(
                json(null, "user_id", "vault_url", "login_path", "creds_path"),
                "`credentials.app_id` not found"
        );
    }

    @Test
    public void should_throw_meaningful_message_when_no_user() {
        expect_init_error_message(
                json("app_id", null, "vault_url", "login_path", "creds_path"),
                "`credentials.user_id` not found"
        );
    }


    @Test
    public void should_return_creds_for_valid_token() {
        JSONObject validEntity = new JSONObject("{ auth: { client_token: 'abcdeftoken' } }");
        VaultCredentialDecoder vc = Mockito.mock(VaultCredentialDecoder.class);
        vc.init(json("app", "user", "vault_url", "login_path", "creds_path"));

        when( vc.fetchToken( any( JSONObject.class ) ) ).thenReturn(validEntity);
        when( vc.getVaultData() ).thenCallRealMethod();
        when( vc.fetchCreds(anyString())).thenReturn(
                new JSONObject("{ data: { value: 'smartwater' } }")
        );
        when( vc.getPassword() ).thenCallRealMethod();

        assertThat(
                vc.getPassword(),
                equalTo("smartwater")
        );
    }

    private JSONObject json(String app, String space, String vault, String login, String creds) {
        return new JSONObject(
                String.format(
                        "{ credentials: { app_id: %s, user_id: %s, vault_url: %s, login_path: %s, creds_path: %s } }",
                        app, space, vault, login, creds
                )
        );
    }


    void expect_init_error_message(JSONObject json, String message) {
        try {
            new VaultCredentialDecoder(json);
            fail("expected error to be thrown");
        } catch (RuntimeException re) {
            assertThat(
                    re.getMessage(),
                    containsString(message)
            );
        }
    }
}
