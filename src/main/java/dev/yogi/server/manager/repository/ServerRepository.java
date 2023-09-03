package dev.yogi.server.manager.repository;

import dev.yogi.server.manager.model.Server;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServerRepository extends JpaRepository<Server, Long> {

    Optional<Server> findByIpAddress(String ipAddress);
    Optional<Server> findByIpAddressAndIdNot(String ipAddress, Long id);
}
