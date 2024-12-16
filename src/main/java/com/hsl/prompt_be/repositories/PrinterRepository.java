package com.hsl.prompt_be.repositories;

import com.hsl.prompt_be.entities.models.Printer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PrinterRepository extends JpaRepository<Printer, UUID> {

    List<Printer> findAllByNameContainingIgnoreCaseOrLocationContainingIgnoreCase(String tag1, String tag2);
}
