package tn.microservices.serviceresultats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.microservices.serviceresultats.entity.Resultat;

import java.util.Optional;

@Repository
public interface ResultatRepository extends JpaRepository<Resultat, String> {
    Optional<Resultat> findBySoutenanceId(String soutenanceId);
}
