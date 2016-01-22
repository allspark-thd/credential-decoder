package dbbroker.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceDemo {

    @Bean
    public DataSource newDataSource () {

        return DataSourceFactory.makeDS("password");

    }

    
}
