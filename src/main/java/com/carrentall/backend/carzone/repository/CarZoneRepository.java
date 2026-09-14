package com.carrentall.backend.carzone.repository;

import com.carrentall.backend.carzone.entity.CarZone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarZoneRepository extends JpaRepository<CarZone, Long> {

    public boolean existsByNameAndAddress(String name , String address);
}
