package dbbroker.client;

import org.json.JSONObject;
import sun.misc.BASE64Decoder;

public class Base64CredentialDecoder implements CredentialDecoder {

    private JSONObject props = null;

    @Override
    public CredentialDecoder init(JSONObject props) {
        this.props = props;
        return this;
    }

    private String extractPassword () {
        try {
            return this.props.getJSONObject("credentials").getString("password");
        } catch ( Exception e ) {}
        return null;
    }
    @Override
    public String getPassword() {
        if (this.props == null || this.extractPassword() == null) {
            return null;
        }

        return "svc-password";
//        return this.props.getString()
//        return new BASE64Decoder().decodeBuffer(this.props.getString("password"));
    }
}
