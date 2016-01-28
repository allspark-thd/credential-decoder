package example.service;

import credentialdecoder.vault.VaultCredentialDecoder;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;


@Service
public class SecureDbDemo {

    private VaultCredentialDecoder vaultDecoderRing = new VaultCredentialDecoder();
    private Logger log = Logger.getLogger(SecureDbDemo.class);

    public SecureDbDemo(VaultCredentialDecoder vc) {
        this.vaultDecoderRing = vc;
    }

    public DataSourceBuilder configureDataSourceBuilder() {

        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();

        try {
            log.info(vaultDecoderRing.getPassword());
            JSONObject creds = new JSONObject(vaultDecoderRing.getPassword());

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

