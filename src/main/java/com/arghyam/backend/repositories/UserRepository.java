package com.arghyam.backend.repositories;

import com.arghyam.backend.entity.Springuser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;


@Repository
public interface UserRepository extends JpaRepository<Springuser, UUID> {
}