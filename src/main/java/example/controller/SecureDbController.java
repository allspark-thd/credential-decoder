package example.controller;

import example.service.SecureDbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecureDbController {

    private final SecureDbService secureDbService;

    @Autowired
    public SecureDbController(SecureDbService secureDbService) {
        this.secureDbService = secureDbService;
    }

    @RequestMapping(value = "/connectToDb")
    public String connect() {
        try {
            secureDbService.configureDataSourceBuilder().build();
            return "I can connect to the db";
        } catch (Exception e) {
            return "I cannot connect to the db";
        }
    }
}
