package com.acm.backend.repository;

import com.acm.backend.model.ConfigDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigRepository extends JpaRepository<ConfigDocument, String> {
}
