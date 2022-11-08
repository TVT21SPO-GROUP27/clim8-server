package fi.clim8.clim8server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.logging.Logger;

@SpringBootApplication
public class Clim8serverApplication {

	public static void main(String[] args) {
        try {
            DataService.getInstance().init();
        } catch(Exception e) {
            Logger.getGlobal().info(e.getMessage());
        }
		SpringApplication.run(Clim8serverApplication.class, args);
	}
}
