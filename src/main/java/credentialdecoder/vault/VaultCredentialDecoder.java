package credentialdecoder.vault;


import credentialdecoder.CredentialDecoder;
import org.apache.commons.codec.Charsets;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.function.Function;

public class VaultCredentialDecoder implements CredentialDecoder {

 private String app, space;

 @Override
 public CredentialDecoder init ( JSONObject props ) {
	this.app = getApp.apply( props );
	this.space = getUser.apply( props );

	return this;
 }

 private static Function< JSONObject, String > getField ( String field ) {
	return ( JSONObject json ) -> {
	 try {
		return json.getJSONObject( "credentials" ).getString( field );
	 } catch ( Exception e ) {
		throw new RuntimeException(
			String.format( "`credentials.%s` not found. `app_id` and `user_id` are required.", field )
		);
	 }
	};
 }

 static final Function< JSONObject, String >
	 getApp = getField( "app_id" ),
	 getUser = getField( "user_id" );

 static final String VAULT = "http://172.24.100.33:8200";
 static final String CREDS_URL = VAULT + "/v1/secret/dbidtest";
 static final String LOGIN_URL = "/v1/auth/app-id/login";
 static final CloseableHttpClient client = HttpClients.createDefault();

 final static RuntimeException responseErr ( HttpResponse response ) {
	return new RuntimeException(
		response.getStatusLine().getStatusCode() +
			" : " +
			response.getStatusLine().getReasonPhrase()
	);
 }

 final static ResponseHandler< JSONObject > toJson =
	 response -> {
		if ( response.getStatusLine().getStatusCode() != 200 ) {
		 throw responseErr( response );
		}
		try {
		 return new JSONObject(
			 EntityUtils.toString( response.getEntity(), Charsets.UTF_8 )
		 );
		} catch ( IOException e ) {
		 throw new RuntimeException( e );
		}
	 };

 final static Function< HttpUriRequest, JSONObject > send = request -> {
	try {
	 return client
		 .execute(
			 request,
			 toJson
		 );
	} catch ( IOException e ) {
	 throw new RuntimeException( e );
	}
 };

 final static Function< JSONObject, HttpEntity > toEntity =
	 json -> new StringEntity( json.toString(), Charsets.UTF_8 );


 final static Function< HttpEntity, RequestBuilder > tokenRequestBuilder =
	 RequestBuilder
		 .post( LOGIN_URL )
		 ::setEntity;

 final static Function< RequestBuilder, HttpUriRequest > buildRequest =
	 RequestBuilder::build;

 final static Function< String, RequestBuilder > credsRequest =
	 token -> RequestBuilder
		 .get( CREDS_URL )
		 .addHeader( "X-Vault-Token", token );

 final static Function< String, Function< JSONObject, JSONObject > >
	 getjson = field -> json -> json.getJSONObject( field );

 final static Function< String, Function< JSONObject, String > > getjsonstr =
	 field -> json -> json.getString( field );

 final static Function extractToken =
	 getjsonstr
		 .apply( "client_token" )
		 .compose( getjson.apply( "auth" ) );

 final static Function< JSONObject, String > getToken =
	 extractToken
		 .compose( send )
		 .compose( buildRequest )
		 .compose( tokenRequestBuilder )
		 .compose( toEntity );

 final static Function< JSONObject, JSONObject > getCreds =
	 getjson
		 .apply( "data" )
		 .compose( send )
		 .compose( buildRequest )
		 .compose( credsRequest )
		 .compose( getToken );

 @Override
 public String getPassword () {
	return getCreds.apply( new JSONObject( "{'app_id':'111111', 'user_id': '999999'}" ) )
		.toString( 2 )
		;
 }
}
