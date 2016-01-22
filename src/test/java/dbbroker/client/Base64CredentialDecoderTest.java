package dbbroker.client;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertTrue;

public class Base64CredentialDecoderTest {

    CredentialDecoder decoder = null;

    @Before
    public void setup () {
        decoder = new Base64CredentialDecoder();
    }

    @Test
    public void should_return_self_on_init () {
        assertTrue( decoder.init(new JSONObject()) == decoder );
    }

    final static String APP_NO_PW = "{ 'name': 'app-no-pw', 'credentials': { 'host': 'example.com', 'protocol': 'https' }, 'syslog_drain_url': '' }";
    final static String APP_WITH_PW = "{ 'name': 'app-with-pw', 'label': 'user-provided', 'tags': [], 'credentials': { 'host': 'hostname.example.com', 'password': 'svc-password', 'protocol': 'https', 'username': 'svc-user' }, 'syslog_drain_url': '' }";
    final static String APP_WITH_ENCODED_PW = "{ 'name': 'app-with-pw', 'label': 'user-provided', 'tags': [], 'credentials': { 'host': 'hostname.example.com', 'password': 'c3ZjLXBhc3N3b3Jk', 'protocol': 'https', 'username': 'svc-user' }, 'syslog_drain_url': '' }";

    @Test
    public void should_return_null_when_no_json () {
        assertThat(
                decoder.init( new JSONObject() ).getPassword(),
                is(equalTo(null))
        );
    }

    @Test
    public void should_return_null_when_no_password () {
        assertThat(
                decoder.init( new JSONObject( APP_NO_PW ) ).getPassword(),
                is( equalTo(null) )
        );
    }

    @Test
    public void should_return_decoded_base64_encoded_password () {
        assertThat(
                decoder.init( new JSONObject( APP_WITH_ENCODED_PW ) ).getPassword(),
                is(equalTo("svc-password"))
        );
    }

}
