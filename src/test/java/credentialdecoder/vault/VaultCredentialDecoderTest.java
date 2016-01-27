package credentialdecoder.vault;

import com.oracle.javafx.jmx.json.JSONFactory;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

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
		new VaultCredentialDecoder().init(
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
