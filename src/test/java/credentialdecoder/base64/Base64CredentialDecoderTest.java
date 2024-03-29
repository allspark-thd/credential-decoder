package credentialdecoder.base64;

import credentialdecoder.CredentialDecoder;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class Base64CredentialDecoderTest {

    final static JSONObject
            EMPTY = new JSONObject(),
            APP_NO_PW = new JSONObject("{ 'name': 'app-no-pw', 'credentials': { 'host': 'example.com', 'protocol': 'https' }, 'syslog_drain_url': '' }"),
            APP_WITH_PLAIN_PW = new JSONObject("{ 'name': 'app-with-pw', 'label': 'user-provided', 'tags': [], 'credentials': { 'host': 'hostname.example.com', 'password': 'plain-password?', 'protocol': 'https', 'username': 'svc-user' }, 'syslog_drain_url': '' }"),
            // 'svc-password'
            APP_WITH_ENCODED_PW = new JSONObject("{ 'name': 'app-with-pw', 'label': 'user-provided', 'tags': [], 'credentials': { 'host': 'hostname.example.com', 'password': 'c3ZjLXBhc3N3b3Jk', 'protocol': 'https', 'username': 'svc-user' }, 'syslog_drain_url': '' }"),
            // 'super_secret!!##'
            APP_WITH_ENCODED_PW2 = new JSONObject("{ 'name': 'app-with-pw', 'label': 'user-provided', 'tags': [], 'credentials': { 'host': 'hostname.example.com', 'password': 'c3VwZXJfc2VjcmV0ISEjIw==', 'protocol': 'https', 'username': 'svc-user' }, 'syslog_drain_url': '' }")
                    ;

    @Test
    public void should_throw_meaningful_message_when_no_json() {
        try {
            new Base64CredentialDecoder(EMPTY);
            fail("no json did not throw");
        } catch (RuntimeException e) {
            assertThat(
                    e.getMessage(),
                    containsString("`credentials.password` not found")
            );
        }
    }

    @Test
    public void should_throw_meaningful_message_when_no_password() {
        try {
            new Base64CredentialDecoder(APP_NO_PW);
            fail("no password did not throw");
        } catch (RuntimeException e) {
            assertThat(
                    e.getMessage(),
                    containsString("`credentials.password` not found")
            );
        }
    }

    @Test
    public void should_return_decoded_base64_encoded_password() {
        assertThat(
                new Base64CredentialDecoder(APP_WITH_ENCODED_PW).getPassword(),
                is(equalTo("svc-password"))
        );
        assertThat(
                new Base64CredentialDecoder(APP_WITH_ENCODED_PW2).getPassword(),
                is(equalTo("super_secret!!##"))
        );
    }

    @Test
    public void should_throw_meaningful_message_when_not_properly_encoded() {
        try {
            new Base64CredentialDecoder(APP_WITH_PLAIN_PW);
            fail("plaintext password did not throw");
        } catch (RuntimeException e) {
            assertThat(
                    e.getMessage(),
                    containsString("must be base64 encoded")
            );
        }
    }

}
