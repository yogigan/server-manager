package dev.yogi.server.manager.controller;

import dev.yogi.server.manager.model.Server;
import dev.yogi.server.manager.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ServerController {

    private final ServerService serverService;

    @QueryMapping
    public List<Server> findAllServers(@Argument int page, @Argument int size) {
        return serverService.findAll(page, size);
    }

    @QueryMapping
    public Server findServerById(@Argument Long id) {
        return serverService.findById(id);
    }

    @QueryMapping
    public Server pingServer(@Argument String ipAddress) {
        return serverService.ping(ipAddress);
    }

    @MutationMapping
    public Server createServer(@Argument Server server) {
        return serverService.create(server);
    }

    @MutationMapping
    public Server updateServer(@Argument Server server) {
        return serverService.update(server);
    }

    @MutationMapping
    public Boolean deleteServer(@Argument Long id) {
        return serverService.delete(id);
    }

    @MutationMapping
    public Boolean saveServers(@Argument List<Server> servers) {
        serverService.saveAll(servers);
        return true;
    }



}
