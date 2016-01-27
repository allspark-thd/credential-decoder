package example.service;

import org.json.JSONObject;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


public class SecureDbDemoTest {

    @Test
    public void should_Create_DataSource_With_Valid_Creds() {

    }

    @Test
    public void should_Return_Null_When_Given_Invalid_Creds() {
        JSONObject props = new JSONObject("{}");
        SecureDbDemo secureDb = new SecureDbDemo(props);
        assertThat(secureDb.dataSource() == null, is(true));
    }
}