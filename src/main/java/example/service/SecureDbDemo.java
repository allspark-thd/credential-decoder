package example.service;

import credentialdecoder.vault.VaultCredentialDecoder;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;


@Service
public class SecureDbDemo {
    private JSONObject credentials;
    private VaultCredentialDecoder vaultDecoderRing;
    private Logger log = Logger.getLogger(SecureDbDemo.class);
    private DataSource ds;

    public SecureDbDemo(JSONObject credentials) {
        log.info("Creating service checker with " + credentials);
        this.credentials = credentials;
        initialize();
    }

    public DataSource dataSource() {
        try {
            log.info(vaultDecoderRing.getPassword());
            DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
            dataSourceBuilder.url(" ");
            dataSourceBuilder.username(" ");
            dataSourceBuilder.password(" ");
            return dataSourceBuilder.build();
        }
        catch(Exception e) {
            log.info(e.getMessage());
            return null;
        }
    }

    private void initialize() {
        try {
            vaultDecoderRing.init(credentials);
            ds = dataSource();
            log.info(credentials);
        }
        catch(Exception e) {
            log.info(e.getMessage());
        }
    }
}

