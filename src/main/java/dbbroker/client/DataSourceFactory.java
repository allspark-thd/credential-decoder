package dbbroker.client;

import javax.sql.DataSource;

public class DataSourceFactory {

    public static DataSource makeDS(String password) {
        return new TestDataSource(password);


        //Get sutfuff from vcap services
        //connect to vault
        //exchange
        //new DataSource (url)

    }
}
