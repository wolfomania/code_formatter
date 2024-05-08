package pl.edu.pja.tpo07.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Repository;
import pl.edu.pja.tpo07.json.adapters.LocalDateTimeAdapter;
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

    private static final String FILE_NAME = "codes.json";

    private Map<String, SavedCode> codes;

    private final ScheduledExecutorService scheduledExecutorService;

    private final Gson gson;

    public CodeRepository() {
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        deserializeData();
        scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        scheduleCodeExpiration();
    }

    public SavedCode getCode(String id) {
        return codes.get(id);
    }

    public void addCode(SavedCode savedCode) {
        codes.computeIfPresent(
                savedCode.getId(),
                (s, savedCode1) -> {
            throw new IllegalArgumentException("Code under key \"" + s + "\" already exists");
        }
        );
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
        try (Writer writer = new FileWriter(FILE_NAME)){
            gson.toJson(codes, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void deserializeData() {
        File file = new File(FILE_NAME);
        if (!file.exists() || file.length() == 0)
            codes = new HashMap<>();
        else
            try (Reader reader = new FileReader(file)){
                codes = gson.fromJson(
                        reader,
                        new TypeToken<HashMap<String, SavedCode>>(){}.getType()
                );
            } catch (IOException ignore) {}
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
