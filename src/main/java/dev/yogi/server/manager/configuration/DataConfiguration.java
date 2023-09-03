package dev.yogi.server.manager.configuration;

import com.github.javafaker.Faker;
import dev.yogi.server.manager.model.Server;
import dev.yogi.server.manager.model.Status;
import dev.yogi.server.manager.service.ServerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;


@Configuration
public class DataConfiguration {

    @Bean
    public Faker faker() {
        return new Faker();
    }

    @Bean
    @Profile("dev")
    public CommandLineRunner commandLineRunner(ServerService serverService, Faker faker) {
        return arg -> {
            List<Server> servers = new ArrayList<>();
            Status[] serverStatus = {
                    Status.SERVER_UP,
                    Status.SERVER_DOWN
            };
            for (int i = 0; i < 10; i++) {

                // Create server
                servers.add(Server.builder()
                        .name(faker.internet().domainName())
                        .ipAddress(faker.internet().ipV4Address())
                        .memory(faker.number().numberBetween(2, 64L) + " GB")
                        .status((serverStatus[faker.random().nextInt(serverStatus.length)]))
                        .type(faker.random().nextBoolean() ? "Linux" : "Windows")
                        .build()
                );

            }

            serverService.saveAll(servers);

        };
    }
}
