package credentialdecoder.vault;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.omg.IOP.ENCODING_CDR_ENCAPS;


import java.io.IOException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

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
	assertThat(
		vc.init(
			json( "app", "user" )
		).getPassword(),
		equalTo( "smartwater" )
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

	HttpClient mockHttpClient;
	VaultCredentialDecoder vc;
	HttpUriRequest validLoginRequest;

	@Before
	public void setup () throws IOException {
		mockHttpClient = Mockito.mock(HttpClient.class);
		vc = new VaultCredentialDecoder(mockHttpClient);
		validLoginRequest = vc.tokenRequest.apply( new JSONObject("{'app_id':'111111', 'user_id': '999999'}") );

		HttpResponse validLoginResponse = Mockito.mock(HttpResponse.class);
		JSONObject validEntity = new JSONObject("{ 'auth': { 'client_token': 'abcdeftoken' } }");
		validLoginResponse.setEntity(
				EntityBuilder.create()
						.setContentType(ContentType.APPLICATION_JSON)
						.setText(validEntity.toString())
						.build()
		);

// 		Mockito.when(validLoginResponse.getEntity()).thenReturn(EntityBuilder.create().setText("{ 'auth': { 'client_token': 'abcdeftoken' } }").build());
//		validLoginResponse.setStatusCode(200);

		Mockito
		.when(mockHttpClient.execute(validLoginRequest))
		.thenReturn(validLoginResponse)
				;
	}

 void expect_init_error_message ( JSONObject json, String message ) {
	try {
	 vc.init( json );
	 fail( "expected error to be thrown" );
	} catch ( RuntimeException re ) {
	 assertThat(
		 re.getMessage(),
		 containsString( message )
	 );
	}
 }
}
