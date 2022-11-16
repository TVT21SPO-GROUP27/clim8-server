package fi.clim8.clim8server;

import fi.clim8.clim8server.data.HadCRUTData;
import fi.clim8.clim8server.user.User;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class RestService {

    @GetMapping("hadcrutdata")
    public ResponseEntity<List<HadCRUTData>> getDataFromV1() {
        return ResponseEntity.of(Optional.of(DatabaseService.getInstance().fecthHadCRUTData()));
    }

    @GetMapping("getAllUsers")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.of(Optional.of(DatabaseService.getInstance().fetchAllUsers()));
    }
/* 
    @PostMapping("users")
    public ResponseEntity<List<User>> createUser(@RequestBody User user) {
        return ResponseEntity.of(Optional.of(DatabaseService.getInstance().addNewUser());
    }*/

    @PostMapping("users")
    public ResponseEntity<String> addNewUser(@RequestParam String username, @RequestParam String email, @RequestParam String password) {
        DatabaseService.getInstance().addNewUser(new User(null, username, email, password));
        return ResponseEntity.ok("Success");
        
    }
}

