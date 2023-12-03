package dev.surya.labs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.surya.labs.entity.Constituency;

public interface ConstituencyRepository extends JpaRepository<Constituency, Long>{

}
