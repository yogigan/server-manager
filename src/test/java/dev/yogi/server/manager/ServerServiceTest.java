package dev.yogi.server.manager;

import dev.yogi.server.manager.exception.BadRequestException;
import dev.yogi.server.manager.exception.InternalServerException;
import dev.yogi.server.manager.exception.NotFoundException;
import dev.yogi.server.manager.model.Server;
import dev.yogi.server.manager.model.Status;
import dev.yogi.server.manager.repository.ServerRepository;
import dev.yogi.server.manager.service.ServerService;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServerServiceTest {

    @Mock
    private ServerRepository serverRepository;

    @InjectMocks
    private ServerService serverService;

    @Order(1)
    @Test
    void testFindAll_Valid() {
        Page<Server> page = mock(Page.class);
        when(serverRepository.findAll(any(PageRequest.class))).thenReturn(page);
        when(page.getContent()).thenReturn(new ArrayList<>());

        List<Server> servers = serverService.findAll(0, 10);

        assertNotNull(servers);
        assertTrue(servers.isEmpty());
    }

    @Order(2)
    @Test
    void testFindById_Valid() {
        Long serverId = 1L;
        Server mockServer = new Server();
        mockServer.setId(serverId);
        when(serverRepository.findById(serverId)).thenReturn(Optional.of(mockServer));

        Server server = serverService.findById(serverId);

        assertNotNull(server);
        assertEquals(serverId, server.getId());
    }

    @Order(3)
    @Test
    void testFindById_NotFound() {
        Long serverId = 1L;
        when(serverRepository.findById(serverId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> serverService.findById(serverId));
    }


    @Test
    void testPing_ServerUp() throws Exception {
        String ipAddress = "127.0.0.1";
        Server mockServer = new Server();
        mockServer.setIpAddress(ipAddress);

        InetAddress inetAddress = mock(InetAddress.class);
        when(serverRepository.findByIpAddress(ipAddress)).thenReturn(Optional.of(mockServer));
        lenient().when(inetAddress.isReachable(5000)).thenReturn(true);

        Server server = serverService.ping(ipAddress);

        assertNotNull(server);
        assertEquals(Status.SERVER_UP, server.getStatus());
        verify(serverRepository).save(mockServer);
    }

    @Order(5)
    @Test
    void testPing_ServerDown() throws Exception {
        String ipAddress = "8.8.8.8";
        Server mockServer = new Server();
        mockServer.setIpAddress(ipAddress);

        InetAddress inetAddress = mock(InetAddress.class);
        when(serverRepository.findByIpAddress(ipAddress)).thenReturn(Optional.of(mockServer));
        lenient().when(inetAddress.isReachable(5000)).thenReturn(false);

        Server server = serverService.ping(ipAddress);

        assertNotNull(server);
        assertEquals(Status.SERVER_DOWN, server.getStatus());
        verify(serverRepository).save(mockServer);
    }


    @Order(5)
    @Test
    void testPing_Error() throws Exception {
        String ipAddress = "0.0.0.1";
        Server mockServer = new Server();
        mockServer.setIpAddress(ipAddress);

        InetAddress inetAddress = mock(InetAddress.class);
        when(serverRepository.findByIpAddress(ipAddress)).thenReturn(Optional.of(mockServer));
        lenient().when(inetAddress.isReachable(5000)).thenReturn(false);

        assertThrows(InternalServerException.class, () -> serverService.ping(ipAddress));
    }


    @Order(6)
    @Test
    void testCreate_Valid() {
        Server newServer = new Server();
        newServer.setIpAddress("127.0.0.1");

        when(serverRepository.findByIpAddress(newServer.getIpAddress())).thenReturn(Optional.empty());
        when(serverRepository.save(newServer)).thenReturn(newServer);

        Server createdServer = serverService.create(newServer);

        assertNotNull(createdServer);
        assertEquals(newServer.getIpAddress(), createdServer.getIpAddress());
    }

    @Order(7)
    @Test
    void testCreate_WithDuplicateIpAddress() {
        Server existingServer = new Server();
        existingServer.setIpAddress("127.0.0.1");

        when(serverRepository.findByIpAddress(existingServer.getIpAddress())).thenReturn(Optional.of(existingServer));

        assertThrows(BadRequestException.class, () -> serverService.create(existingServer));
    }

    @Order(8)
    @Test
    void testSaveAll_Valid() {
        List<Server> serversToSave = new ArrayList<>();
        Server server1 = new Server();
        server1.setIpAddress("127.0.0.1");
        serversToSave.add(server1);

        Server server2 = new Server();
        server2.setIpAddress("192.168.1.1");
        serversToSave.add(server2);

        when(serverRepository.findByIpAddress("127.0.0.1")).thenReturn(Optional.empty());
        when(serverRepository.findByIpAddress("192.168.1.1")).thenReturn(Optional.empty());

        when(serverRepository.saveAll(serversToSave)).thenReturn(serversToSave);

        serverService.saveAll(serversToSave);

        verify(serverRepository).saveAll(serversToSave);
    }

    @Order(9)
    @Test
    void testSaveAll_WithDuplicateIpAddress() {
        List<Server> serversToSave = new ArrayList<>();
        Server server1 = new Server();
        server1.setIpAddress("127.0.0.1");
        serversToSave.add(server1);

        Server server2 = new Server();
        server2.setIpAddress("127.0.0.1");
        serversToSave.add(server2);

        when(serverRepository.findByIpAddress("127.0.0.1")).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> serverService.saveAll(serversToSave));
    }


    @Order(10)
    @Test
    void testSaveAll_IpAddressAlreadyExist() {
        List<Server> serversToSave = new ArrayList<>();
        Server server1 = new Server();
        server1.setIpAddress("127.0.0.1");
        serversToSave.add(server1);

        Server server2 = new Server();
        server2.setIpAddress("192.168.1.1");
        serversToSave.add(server2);

        when(serverRepository.findByIpAddress("127.0.0.1")).thenReturn(Optional.empty());
        when(serverRepository.findByIpAddress("192.168.1.1")).thenReturn(Optional.of(server2));

        assertThrows(BadRequestException.class, () -> serverService.saveAll(serversToSave));
    }

    @Order(11)
    @Test
    void testUpdate() {
        Long serverId = 1L;
        Server existingServer = new Server();
        existingServer.setId(serverId);
        existingServer.setIpAddress("127.0.0.1");

        Server updatedServer = new Server();
        updatedServer.setId(serverId);
        updatedServer.setIpAddress("192.168.1.1");

        when(serverRepository.findById(serverId)).thenReturn(Optional.of(existingServer));
        when(serverRepository.findByIpAddress(updatedServer.getIpAddress())).thenReturn(Optional.empty());
        when(serverRepository.save(existingServer)).thenReturn(existingServer);

        Server result = serverService.update(updatedServer);

        assertNotNull(result);
        assertEquals(updatedServer.getIpAddress(), result.getIpAddress());
    }

    @Order(12)
    @Test
    void testUpdate_NotFound() {
        Server updatedServer = new Server();
        updatedServer.setId(1L);

        when(serverRepository.findById(updatedServer.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> serverService.update(updatedServer));
    }

    @Order(13)
    @Test
    void testUpdate_WithDuplicateIpAddress() {
        Long serverId = 1L;
        Server existingServer = new Server();
        existingServer.setId(serverId);
        existingServer.setIpAddress("127.0.0.1");

        Server updatedServer = new Server();
        updatedServer.setId(serverId);
        updatedServer.setIpAddress("127.0.0.2");

        when(serverRepository.findById(serverId)).thenReturn(Optional.of(existingServer));
        when(serverRepository.findByIpAddress(updatedServer.getIpAddress())).thenReturn(Optional.of(existingServer));

        assertThrows(BadRequestException.class, () -> serverService.update(updatedServer));
    }

    @Order(14)
    @Test
    void testDelete() {
        Long serverId = 1L;
        Server serverToDelete = new Server();
        serverToDelete.setId(serverId);

        when(serverRepository.findById(serverId)).thenReturn(Optional.of(serverToDelete));

        boolean result = serverService.delete(serverId);

        assertTrue(result);
        verify(serverRepository).delete(serverToDelete);
    }

    @Order(15)
    @Test
    void testDeleteServerNotFound() {
        Long serverId = 1L;

        when(serverRepository.findById(serverId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> serverService.delete(serverId));
    }

    @Order(16)
    @Test
    void testFindServerByIpAddress() {
        String ipAddress = "127.0.0.1";
        Server mockServer = new Server();
        mockServer.setIpAddress(ipAddress);

        when(serverRepository.findByIpAddress(ipAddress)).thenReturn(Optional.of(mockServer));

        Server server = serverService.findServerByIpAddress(ipAddress);

        assertNotNull(server);
        assertEquals(ipAddress, server.getIpAddress());
    }

    @Order(17)
    @Test
    void testFindServerByIpAddressNotFound() {
        String ipAddress = "127.0.0.1";

        when(serverRepository.findByIpAddress(ipAddress)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> serverService.findServerByIpAddress(ipAddress));
    }
}
