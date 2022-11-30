package fi.clim8.clim8server;

import fi.clim8.clim8server.data.HadCRUTData;
//import fi.clim8.clim8server.jwt.JwtRequest;
import fi.clim8.clim8server.jwt.JwtResponse;
import fi.clim8.clim8server.jwt.JwtToken;
import fi.clim8.clim8server.user.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<String> addNewUser(@RequestBody User user) {
        DatabaseService.getInstance().addNewUser(user);
        return ResponseEntity.ok("Success");
        
    }

    @Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtToken jwtTokenUtil;

	@Autowired
	private UserDetailsService jwtInMemoryUserDetailsService;

	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody User authenticationRequest)
			throws Exception {

		authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

		final UserDetails userDetails = jwtInMemoryUserDetailsService
				.loadUserByUsername(authenticationRequest.getUsername());

		final String token = jwtTokenUtil.generateToken(userDetails);

		return ResponseEntity.ok(new JwtResponse(token));
	}

	private void authenticate(String username, String password) throws Exception {
		Objects.requireNonNull(username);
		Objects.requireNonNull(password);

		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
	}
}
