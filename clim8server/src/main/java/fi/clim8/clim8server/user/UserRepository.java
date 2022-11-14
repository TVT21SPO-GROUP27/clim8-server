package fi.clim8.clim8server.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository <User, Long> {
    
    @Query("SELECT s FROM User s WHERE s.email =?1")
    Optional<User> findUserByEmail(String email);
}
