package dev.yogi.server.manager.service;

import dev.yogi.server.manager.exception.BadRequestException;
import dev.yogi.server.manager.exception.InternalServerException;
import dev.yogi.server.manager.exception.NotFoundException;
import dev.yogi.server.manager.model.Server;
import dev.yogi.server.manager.model.Status;
import dev.yogi.server.manager.repository.ServerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ServerService {

    private final ServerRepository serverRepository;


    public List<Server> findAll(int page, int size) {
        log.info("Finding all servers");
        return serverRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Order.asc("id"))))
                .getContent();
    }

    public Server findById(Long id) {
        log.info("Finding server by id: {}", id);
        return serverRepository.findById(id).orElseThrow(() -> {
            log.error("Server not found");
            return new NotFoundException("Server not found");
        });
    }

    public Server ping(String ipAddress) {
        log.info("Pinging server: {}", ipAddress);

        // check if server with ip address exists
        Server server = findServerByIpAddress(ipAddress);
        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            server.setStatus(inetAddress.isReachable(5000) ? Status.SERVER_UP : Status.SERVER_DOWN);
            serverRepository.save(server);
            return server;
        } catch (IOException e) {
            throw new InternalServerException("Error pinging server : " + e.getMessage());
        }
    }

    public Server create(Server server) {
        log.info("Creating server: {}", server);

        // check if server with ip address already exists
        serverRepository.findByIpAddress(server.getIpAddress()).ifPresent(s -> {
            throw new BadRequestException("Server with ip address " + server.getIpAddress() + " already exists");
        });
        return serverRepository.save(server);
    }

    public void saveAll(List<Server> servers) {
        log.info("Saving all servers");
        List<String> ipAddresses = new ArrayList<>();

        // check if server with ip address already exists
        servers.forEach(server -> {
            serverRepository.findByIpAddress(server.getIpAddress()).ifPresent(s -> {
                throw new BadRequestException("Server with ip address " + server.getIpAddress() + " already exists");
            });

            // check if ip address is not unique
            if (ipAddresses.contains(server.getIpAddress())) {
                throw new BadRequestException("Server with ip address " + server.getIpAddress() + " already exists");
            } else {
                ipAddresses.add(server.getIpAddress());
            }
        });


        serverRepository.saveAll(servers);
    }

    public Server update(Server server) {
        log.info("Updating server: {}", server);

        // check if server with id exists
        Server serverToUpdate = serverRepository.findById(server.getId()).orElseThrow(() -> {
            throw new NotFoundException("Server not found");
        });

        // check if server with ip address already exists
        serverRepository.findByIpAddressAndIdNot(server.getIpAddress(), server.getId()).ifPresent(s -> {
            throw new BadRequestException("Server with ip address " + server.getIpAddress() + " already exists");
        });

        // Update the server object with the new values
        serverToUpdate.setIpAddress(server.getIpAddress());
        serverToUpdate.setName(server.getName());
        serverToUpdate.setMemory(server.getMemory());
        serverToUpdate.setType(server.getType());
        serverToUpdate.setStatus(server.getStatus());
        return serverRepository.save(serverToUpdate);
    }


    public boolean delete(Long id) {
        log.info("Deleting server by id: {}", id);
        Server server = serverRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Server not found");
        });
        serverRepository.delete(server);
        return true;
    }

    public Server findServerByIpAddress(String ipAddress) {
        log.info("Finding server by ip address: {}", ipAddress);
        return serverRepository.findByIpAddress(ipAddress).orElseThrow(() -> {
            throw new NotFoundException("Server with ip address " + ipAddress + " not found");
        });
    }


}
