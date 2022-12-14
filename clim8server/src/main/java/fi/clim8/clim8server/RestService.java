package fi.clim8.clim8server;

import fi.clim8.clim8server.data.*;
import fi.clim8.clim8server.user.User;

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

    @GetMapping("mobergdata")
    public ResponseEntity<List<AbstractData>> getDataFromV2() {
        return ResponseEntity.of(Optional.of(DatabaseService.getInstance().fetchMoberg2005Data()));
    }
    @GetMapping("maunaloadata")
    public ResponseEntity<List<MaunaLoaData>> getDataFromV3() {
        return ResponseEntity.of(Optional.of(DatabaseService.getInstance().fetchMaunaLoaData()));
    }
    @GetMapping("icecoredata")
    public ResponseEntity<List<IceCoreData>> getDataFromV4() {
        return ResponseEntity.of(Optional.of(DatabaseService.getInstance().fetchIceCoreData()));
    }

    @GetMapping("vostokcoredata")
    public ResponseEntity<List<VostokData>> getDataFromV5() {
        return ResponseEntity.of(Optional.of(DatabaseService.getInstance().fetchVostokCoreData()));
    }

    @GetMapping("acoredata")
    public ResponseEntity<List<ACoreRevised>> getDataFromV6() {
        return ResponseEntity.of(Optional.of(DatabaseService.getInstance().fetchACoreData()));
    }

    @GetMapping("snydertemp")
    public ResponseEntity<List<Snyder>> getDataFromV7temp() {
        return ResponseEntity.of(Optional.of(DatabaseService.getInstance().fetchSnyderTemp()));
    }

    @GetMapping("snyderco2")
    public ResponseEntity<List<Snyder>> getDataFromV7co2() {
        return ResponseEntity.of(Optional.of(DatabaseService.getInstance().fetchSnyderCo2()));
    }

    @GetMapping("nationalcarbonemissions")
    public ResponseEntity<List<NationalCarbonData>> getDataFromV8() {
        return ResponseEntity.of(Optional.of(DatabaseService.getInstance().fetchNationalCarbonData()));
    }


    @GetMapping("getAllUsers")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.of(Optional.of(DatabaseService.getInstance().fetchAllUsers()));
    }

    @GetMapping("usersid")
    public ResponseEntity<List<User>> getUserById(User user, @RequestParam Long id) {
        return ResponseEntity.of(Optional.of(DatabaseService.getInstance().getUserById(user)));    
    }

    @GetMapping("usersname")
    public ResponseEntity<List<User>> getUserByName(User user, @RequestParam String name) {
        return ResponseEntity.of(Optional.of(DatabaseService.getInstance().getUserByName(user)));    
    }
     
    @DeleteMapping("users")
    public ResponseEntity<String> deleteUser(User user, @RequestParam Long id) {
        DatabaseService.getInstance().deleteUser(user);
        return ResponseEntity.ok("User deleted succesfully.");
    } 
    /*
     * @PostMapping("users")
     * public ResponseEntity<List<User>> createUser(@RequestBody User user) {
     * return
     * ResponseEntity.of(Optional.of(DatabaseService.getInstance().addNewUser());
     * }
     */

    @PostMapping("users")
    public ResponseEntity<String> addNewUser(@RequestBody User user) {
        DatabaseService.getInstance().addNewUser(user);
        return ResponseEntity.ok("Success");
    }

    @PostMapping("login")
    public ResponseEntity<Boolean> login(@RequestBody User user){
        DatabaseService.getInstance().login(user);
        return ResponseEntity.of(Optional.of(DatabaseService.getInstance().login(user))); 
    }


}
