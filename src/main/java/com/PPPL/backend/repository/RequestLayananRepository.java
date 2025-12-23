package com.PPPL.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.PPPL.backend.model.RequestLayanan;
import com.PPPL.backend.model.StatusRequest;

import java.util.List;

@Repository
public interface RequestLayananRepository extends JpaRepository<RequestLayanan, Integer> {

    List<RequestLayanan> findByStatus(StatusRequest status);

    List<RequestLayanan> findByStatusOrderByTglRequestAsc(StatusRequest status);

    long countByStatus(StatusRequest status);
}
