package tn.microservices.serviceplanification.service;

import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class ConflitService {

    public boolean chevauchement(LocalTime d1, LocalTime f1,
                                 LocalTime d2, LocalTime f2) {

        return d1.isBefore(f2) && d2.isBefore(f1);
    }
}