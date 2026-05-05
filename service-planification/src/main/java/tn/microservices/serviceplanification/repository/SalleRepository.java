package tn.microservices.serviceplanification.repository;

import tn.microservices.serviceplanification.entity.Salle;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SalleRepository extends JpaRepository<Salle, Long> {
}