package pl.edu.pja.tpo07.repository;

import org.springframework.stereotype.Repository;
import pl.edu.pja.tpo07.models.SavedCode;

import java.io.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Repository
public class CodeRepository {

    private final String FILENAME = "codes.dat";

    private Map<String, SavedCode> codes;

    private final ScheduledExecutorService scheduledExecutorService;

    public CodeRepository() {
        deserializeData();
        scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        scheduleCodeExpiration();
    }

    public SavedCode getCode(String id) {
        return codes.get(id);
    }

    public void addCode(SavedCode savedCode) {
        codes.computeIfPresent(savedCode.getId(), (s, savedCode1) -> {
            throw new IllegalArgumentException("Code under key \"" + s + "\" already exists");
        });
        codes.put(savedCode.getId(), savedCode);
        scheduledExecutorService.schedule(
                () -> removeCode(savedCode.getId()),
                LocalDateTime.now().until(
                        savedCode.getExpirationDate(),
                        ChronoUnit.SECONDS
                ),
                TimeUnit.SECONDS
        );
        serializeData();
    }

    private void removeCode(String id) {
        codes.remove(id);
        serializeData();
    }

    private void serializeData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILENAME))){
            oos.writeObject(codes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void deserializeData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILENAME))){
            codes = (HashMap<String, SavedCode>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            codes = new HashMap<>();
        }
    }

    private void scheduleCodeExpiration() {
        for (Map.Entry<String, SavedCode> entry : codes.entrySet()) {
            scheduledExecutorService.schedule(
                    () -> removeCode(entry.getKey()),
                    LocalDateTime.now().until(
                            entry.getValue().getExpirationDate(),
                            ChronoUnit.SECONDS
                    ),
                    TimeUnit.SECONDS
            );
        }
    }
}
