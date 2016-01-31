package example.service;

import credentialdecoder.CredentialDecoder;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.stereotype.Service;

@Service
public class SecureDbService {

    private CredentialDecoder decoderRing;
    private Logger log = Logger.getLogger(SecureDbService.class);

    public SecureDbService(CredentialDecoder vc) {
        this.decoderRing = vc;
    }

    public SecureDbService() {

    }

    public DataSourceBuilder configureDataSourceBuilder() {

        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();

        try {
            log.info(decoderRing.getPassword());
            JSONObject creds = new JSONObject(decoderRing.getPassword());

            log.info(creds.getString("url"));
            log.info(creds.getString("user"));
            log.info(creds.get("password"));

            dataSourceBuilder.driverClassName("org.mariadb.jdbc.Driver");
            dataSourceBuilder.url(creds.getString("url"));
            dataSourceBuilder.username(creds.getString("user"));
            dataSourceBuilder.password(creds.getString("password"));

        } catch (Exception e) {
            log.error("Unable to create datasource with credentials - " + e.getMessage());
            return null;
        }
        return dataSourceBuilder;
    }
}

