package dbbroker.client;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class CredentialFactoryTest {
    String pw;

    @Before
    public void setup() {
        pw = CredentialFactory.getPassword("app", "space", "username");
    }

    @Test
    public void testGetPassword() throws Exception {
        assertNotNull(pw);
    }

    @Test
    public void testPasswordIsPassword() {
        assertThat(
                pw,
                is("password")
        );
    }
}
