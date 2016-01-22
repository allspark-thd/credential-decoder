package dbbroker.client;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

public class DataSourceDemoTest {

    private TestDataSource dataSource = null;

    @Before
    public void setUp() {
        this.dataSource = (TestDataSource) new DataSourceDemo().newDataSource();
    }
    @Test
    public void testNewDataSource() throws Exception {
        assertNotNull(dataSource);
    }

    @Test
    public void testPassword() {
        assertThat(dataSource.password, is(equalTo("password")));
    }
}

/**
 pcf app1 ('111111') in pcf space1 ('999999'). Vault has a make believe data source account 'dbidtest' password vaulted.

 // Grab an auth token from vault
 [root@ld0017 ~]# curl 172.24.100.33:8200/v1/auth/app-id/login -d '{"app_id":"111111", "user_id":"999999"}'
 */