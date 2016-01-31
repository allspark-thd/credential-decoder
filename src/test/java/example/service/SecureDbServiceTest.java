package example.service;

import credentialdecoder.vault.VaultCredentialDecoder;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


public class SecureDbServiceTest {

    @Mock
    private VaultCredentialDecoder vaultCredentialDecoder;
    
    @Before
    public void setup() throws Exception{
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void should_Create_DataSource_With_Valid_Creds() {
        SecureDbService secureDB = new SecureDbService(vaultCredentialDecoder);
        JSONObject validResponse = new JSONObject("{ user: 'smartwater', password: 'password', url:'jdbc:mariadb://target;AUTO_RECONNECT=TRUE' } ");

        Mockito.when(vaultCredentialDecoder.getPassword()).thenReturn(validResponse.toString());
        assertNotNull(secureDB.configureDataSourceBuilder());
    }

    @Test
    public void should_Return_Null_When_Given_Invalid_Creds() {
        SecureDbService secureDb = new SecureDbService(vaultCredentialDecoder);
        assertNull(secureDb.configureDataSourceBuilder());
    }

}