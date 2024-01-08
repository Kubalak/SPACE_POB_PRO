package pl.psk.termdemo.ssh;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.common.keyprovider.FileKeyPairProvider;
import org.apache.sshd.server.SshServer;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Klasa implementująca serwer SSH.
 */
@Slf4j
public class SSHServer {

    private static final SshServer sshd = SshServer.setUpDefaultServer();

    /**
     * Główna funkcja z serwerem SSH.
     * @param args Argumenty uruchomieniowe programu.
     */
    public static void main(String[] args) {
        try {
            startSshServer();
        } catch (IOException e) {
            log.error("Error occurred while starting SSH server: {}", e.getMessage());
        }
    }

    private static void startSshServer() throws IOException {
        final Path pathToKey = Paths.get("hostkey.ser");
        final Path pathToJsonFile = Paths.get("auth.json");
        final Map<String, String> credentials = loadCredentialsFromJsonFile(pathToJsonFile);

        final ExecutorService service = Executors.newSingleThreadExecutor();
        final Object lock = new Object();

        sshd.setPort(22);
        sshd.setKeyPairProvider(new FileKeyPairProvider(pathToKey));
        sshd.setPasswordAuthenticator((username, password, session) -> {
            String storedPassword = credentials.get(username);
            return storedPassword != null && storedPassword.equals(password);
        });

        sshd.setShellFactory(new VTClientShellFactory());
        sshd.start();

        log.info("SSH server was started on port {}", sshd.getPort());



        service.submit(() -> {
            try {
                synchronized (lock) {
                    while (!Thread.currentThread().isInterrupted()) {
                        lock.wait(10000);
                    }
                }
            } catch (InterruptedException e) {
                log.info("Server has been stopped.");
                Thread.currentThread().interrupt();
            }
        });

        boolean isTerminated = false;
        try {
            isTerminated = service.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            log.error("Error occurred: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }

        if (isTerminated) {
            log.info("ExecutorService has terminated.");
        } else {
            log.info("ExecutorService has not terminated.");
        }

        service.shutdown();
        sshd.stop();
    }


    @SneakyThrows
    private static Map<String, String> loadCredentialsFromJsonFile(final Path filePath) {
        Map<String, String> credentials = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(filePath.toFile());
            Iterator<Map.Entry<String, JsonNode>> fields = rootNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                if (entry.getValue().isTextual()) {
                    credentials.put(entry.getKey(), entry.getValue().asText());
                }
            }
        } catch (IOException e) {
            log.error("Error occurred while reading JSON file: {}", e.getMessage());
        }
        return credentials;
    }
}


