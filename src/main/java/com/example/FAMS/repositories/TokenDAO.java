package com.example.FAMS.repositories;

import com.example.FAMS.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenDAO extends JpaRepository<Token, Integer> {

    Optional<Token> findByToken(String token);

    @Query("""
                    SELECT t FROM Token t INNER JOIN User u ON t.user.userId = u.userId
                    WHERE u.userId = :userId AND (t.expired = FALSE OR t.revoked = FALSE)
            """)
    List<Token> findAllUserTokenByUserId(Integer userId);
}
