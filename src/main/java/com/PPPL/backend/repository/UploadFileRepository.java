package com.PPPL.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.PPPL.backend.model.file.UploadFile;

@Repository
public interface UploadFileRepository extends JpaRepository<UploadFile, Long> {}
