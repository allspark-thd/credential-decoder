package example.config;

import example.service.SecureDbService;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SecureDbServiceConfigTest {

    /**
     * WARNING!
     * This test assumes you don't have VCAP_SERVICES set in your
     * environment when running it!
     */
    @Test
    public void it_handles_null_vcapServices() {
        SecureDbServiceConfig config = new SecureDbServiceConfig();
        SecureDbService dbDemo = config.secureDbDemo();
        assertThat(dbDemo.configureDataSourceBuilder() == null, is(true));
    }
}