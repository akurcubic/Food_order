package com.raf.foodOrder.repository;

import com.raf.foodOrder.model.ErrorMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErrorMessageRepository extends JpaRepository<ErrorMessage, Integer> {

    Page<ErrorMessage> findByUserId(int userId, Pageable pageable);
}
