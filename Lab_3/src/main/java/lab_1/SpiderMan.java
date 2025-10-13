package lab_1;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SpiderMan {
    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(args[0]);
        String baseUrl = "http://localhost:" + port;
        ObjectMapper mapper = new ObjectMapper();
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(12)).build();
            ConcurrentMap<String, Boolean> visited = new ConcurrentHashMap<>();
            ConcurrentLinkedQueue<String> collectedMessages = new ConcurrentLinkedQueue<>();
            AtomicInteger activeTasks = new AtomicInteger(0);
            CompletableFuture<Void> finished = new CompletableFuture<>();
            class Submitter {
                void submit(String path) {
                    if(path==null||path.isEmpty()){
                        path="/";
                    }
                    if(!path.startsWith("/")){
                        path="/"+path;
                    }
                    if(visited.putIfAbsent(path, Boolean.TRUE)!=null){
                        return;
                    }
                    activeTasks.incrementAndGet();
                    String finalPath = path;
                    executor.execute(() -> {
                        fetchAndProcess(finalPath);
                        if(activeTasks.decrementAndGet()==0){
                            finished.complete(null);
                        }
                    });
                }

                void fetchAndProcess(String path) {
                    URI uri = URI.create(baseUrl + path);
                    HttpRequest request = HttpRequest.newBuilder().uri(uri).timeout(Duration.ofSeconds(20)).GET().build();
                    try {
                        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                        if(response.statusCode()!=200){
                            System.err.println("Not 200 for"+path+" "+response.statusCode());
                            return;
                        }
                        Map<String, Object> data = mapper.readValue(response.body(), new TypeReference<Map<String, Object>>() {});
                        collectedMessages.add((String)data.get("message"));
                        List<String> succList = (List<String>)data.get("successors");
                        for(String s : succList){
                            submit(s);
                        }
                    } catch (HttpTimeoutException te) {
                        System.err.println("Timeout"+path+" "+te.getMessage());
                    } catch (Exception ex) {
                        System.err.println("Error"+path+ex.getMessage());
                    }
                }
            }
            Submitter submitter = new Submitter();
            submitter.submit("/");
            try {
                finished.get(120, TimeUnit.SECONDS);
            } catch (TimeoutException te) {
                System.err.println("Timeout after 120 seconds");
            } catch (ExecutionException ee) {
                System.err.println("Executor error");
            }
            List<String> result = new ArrayList<>(collectedMessages);
            Collections.sort(result);
            System.out.println("Get"+result.size()+"messages");
            for (String s : result) {
                System.out.println(s);
            }
        }
    }
}
