package com.onlineshop.Online.shop.Repository;

import com.onlineshop.Online.shop.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);  // Pronalazi korisnika po korisničkom imenu
    Optional<User> findByEmail(String email);
    boolean existsByEmailAndProviderNot(String email, String provider);
    Boolean existsByUsername(String username);       // Proverava da li korisnik sa datim korisničkim imenom postoji
    List<User> findByRole(String role);  // Pronalazi sve korisnike sa određenom ulogom

}
