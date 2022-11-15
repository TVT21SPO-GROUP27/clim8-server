package fi.clim8.clim8server;

import fi.clim8.clim8server.data.HadCRUTData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class RestService {

    @GetMapping("hadcrutdata")
    public ResponseEntity<List<HadCRUTData>> getDataFromV1(){
        return ResponseEntity.of(Optional.of(DatabaseService.getInstance().fecthHadCRUTData()));
    }
}
