package example.service;

import example.service.util.VaultHelper;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;


@Service
public class SecureDbDemo {
    private JSONObject credentials;
    private Logger log = Logger.getLogger(SecureDbDemo.class);
    private DataSource ds;

    public SecureDbDemo(JSONObject credentials) {
        log.info("Creating service checker with " + credentials);
        this.credentials = credentials;
        initialize();
    }

    public DataSource dataSource(JSONObject credentials) {
        VaultHelper vault = new VaultHelper(credentials);
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.url(vault.getUrl().toString());
        dataSourceBuilder.username(vault.getUser());
        dataSourceBuilder.password(vault.getPassword());
        return dataSourceBuilder.build();
    }

    private void initialize() {
        //ds = dataSource(credentials);
        log.info(credentials);
    }
}

