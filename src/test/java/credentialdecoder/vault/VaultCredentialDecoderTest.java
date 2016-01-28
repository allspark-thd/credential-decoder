package credentialdecoder.vault;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.omg.IOP.ENCODING_CDR_ENCAPS;


import java.io.IOException;

import static credentialdecoder.vault.VaultCredentialDecoder.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VaultCredentialDecoderTest {
 @Test
 public void should_throw_meaningful_message_when_null () {
	expect_init_error_message(
		null,
		"`app_id` and `user_id` are required"
	);
 }

 @Test
 public void should_throw_meaningful_message_when_no_json () {
	expect_init_error_message(
		new JSONObject(),
		"`app_id` and `user_id` are required"
	);
 }

 @Test
 public void should_throw_meaningful_message_when_no_application () {
	expect_init_error_message(
		json( null, "userid" ),
		"`credentials.app_id` not found"
	);
 }

 @Test
 public void should_throw_meaningful_message_when_no_user () {
	expect_init_error_message(
		json( "appid", null ),
		"`credentials.user_id` not found"
	);
 }


 @Test
 public void valid () {
	JSONObject validEntity = new JSONObject( "{ auth: { client_token: 'abcdeftoken' } }" );
	VaultCredentialDecoder vc = new VaultCredentialDecoder();
	vc.fetchToken = creds -> validEntity;
	vc.fetchCreds = token -> new JSONObject( "{ data: { value: 'smartwater' } }" );
		;
//	vc.getToken = x -> extractToken.apply( validEntity );

	assertThat(
		vc.init(
			json( "app", "user" )
		).getPassword(),
		equalTo( new JSONObject( "{ value: 'smartwater' }" ).toString() )
	);
 }


 private String jsonstr ( String value ) {
	return value == null ? "null" : String.format( "'%s'", value );
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

 void expect_init_error_message ( JSONObject json, String message ) {
	try {
	 new VaultCredentialDecoder().init( json );
	 fail( "expected error to be thrown" );
	} catch ( RuntimeException re ) {
	 assertThat(
		 re.getMessage(),
		 containsString( message )
	 );
	}
 }
}
