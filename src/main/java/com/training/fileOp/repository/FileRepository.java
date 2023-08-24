package com.training.fileOp.repository;

import com.training.fileOp.entity.FileOP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The interface User repository.
 *
 * @author Givantha Kalansuriya
 */
@Repository
public interface FileRepository extends JpaRepository<FileOP, Long> {

}