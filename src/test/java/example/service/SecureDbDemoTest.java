package example.service;

import credentialdecoder.vault.VaultCredentialDecoder;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;


public class SecureDbDemoTest {

    @Mock
    private VaultCredentialDecoder vaultCredentialDecoder;
    
    @Before
    public void setup() throws Exception{
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void should_Create_DataSource_With_Valid_Creds() {
        SecureDbDemo secureDB = new SecureDbDemo(vaultCredentialDecoder);
        JSONObject validResponse = new JSONObject("{ user: 'smartwater', password: 'password', url:'jdbc:mariadb://target;AUTO_RECONNECT=TRUE' } ");

        Mockito.when(vaultCredentialDecoder.getPassword()).thenReturn(validResponse.toString());
        assertThat(secureDB.configureDataSourceBuilder()==null, equalTo(false));
    }

    @Test
    public void should_Return_Null_When_Given_Invalid_Creds() {
        SecureDbDemo secureDb = new SecureDbDemo(vaultCredentialDecoder);
        assertThat(secureDb.configureDataSourceBuilder()==null, equalTo(true));
    }

}