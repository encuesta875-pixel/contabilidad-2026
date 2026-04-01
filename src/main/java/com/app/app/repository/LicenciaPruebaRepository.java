package com.app.app.repository;

import com.app.app.model.LicenciaPrueba;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LicenciaPruebaRepository extends JpaRepository<LicenciaPrueba, Long> {

    Optional<LicenciaPrueba> findByMacAddress(String macAddress);

    Optional<LicenciaPrueba> findByMacAddressAndActivaTrue(String macAddress);

    boolean existsByMacAddress(String macAddress);
}
