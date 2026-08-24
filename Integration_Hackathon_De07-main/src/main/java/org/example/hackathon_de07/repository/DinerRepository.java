package org.example.hackathon_de07.repository;

import org.example.hackathon_de07.model.entity.Diner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DinerRepository extends JpaRepository<Diner, Long> {
    Optional<Diner> findByPhone(String phone);
}
