package tn.microservices.serviceplanification.repository;

import tn.microservices.serviceplanification.entity.Creneau;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CreneauRepository extends JpaRepository<Creneau, Long> {
}