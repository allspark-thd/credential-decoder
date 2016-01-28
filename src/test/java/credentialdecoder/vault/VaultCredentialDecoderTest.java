package credentialdecoder.vault;

import org.json.JSONObject;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class VaultCredentialDecoderTest {
    @Test
    public void should_throw_meaningful_message_when_null() {
        expect_init_error_message(
                null,
                "`app_id` and `user_id` are required"
        );
    }

    @Test
    public void should_throw_meaningful_message_when_no_json() {
        expect_init_error_message(
                new JSONObject(),
                "`app_id` and `user_id` are required"
        );
    }

    @Test
    public void should_throw_meaningful_message_when_no_application() {
        expect_init_error_message(
                json(null, "userid"),
                "`credentials.app_id` not found"
        );
    }

    @Test
    public void should_throw_meaningful_message_when_no_user() {
        expect_init_error_message(
                json("appid", null),
                "`credentials.user_id` not found"
        );
    }


 @Test
 public void should_return_creds_for_valid_token () {
	JSONObject validEntity = new JSONObject( "{ auth: { client_token: 'abcdeftoken' } }" );
	VaultCredentialDecoder vc = new VaultCredentialDecoder();
	vc.fetchToken = creds -> validEntity;
	vc.fetchCreds = token -> new JSONObject( "{ data: { value: 'smartwater' } }" );

        assertThat(
                vc.init(
                        json("app", "user")
                ).getPassword(),
                equalTo(new JSONObject("{ value: 'smartwater' }").toString())
        );
    }

 private JSONObject json ( String app, String space ) {
	return new JSONObject(
		String.format(
			"{ credentials: { app_id: %s, user_id: %s } }",
			app,
			space
		)
	);
 }


    void expect_init_error_message(JSONObject json, String message) {
        try {
            new VaultCredentialDecoder().init(json);
            fail("expected error to be thrown");
        } catch (RuntimeException re) {
            assertThat(
                    re.getMessage(),
                    containsString(message)
            );
        }
    }
}
