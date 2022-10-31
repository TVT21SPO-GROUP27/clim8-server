package fi.clim8.clim8server;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class RestService {

    @GetMapping("data")
    public ResponseEntity<String> getDataFromV1(){
        return ResponseEntity.ok("OK!");
    }
}
