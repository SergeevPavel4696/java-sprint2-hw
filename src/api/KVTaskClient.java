package api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    public HttpClient client;
    private String token;
    private final URI url;

    public KVTaskClient(String url) {
        this.client = HttpClient.newHttpClient();
        this.url = URI.create(url);
        HttpRequest request = HttpRequest
                .newBuilder()
                .GET()
                .uri(URI.create(url + "/register"))
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request, handler);
            if (response.statusCode() == 200) {
                this.token = response.body();
            } else {
                System.out.println("Ошибка регистрации.\n" + "Код ошибки - \"" + response.statusCode() + "\".");
            }
        } catch (NullPointerException | IOException | InterruptedException e) {
            System.out.println("Ошибка регистрации.");
        }
    }

    public void put(String key, String json) {
        final HttpRequest.BodyPublisher requestBody = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest
                .newBuilder()
                .POST(requestBody)
                .uri(URI.create(url + "/save/" + key + "/?token=" + token))
                .build();
        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println("Состояние менеджера задач сохранено");
            } else {
                System.out.println("Ошибка сохранения состояния.\n" + "Код ошибки - \"" + response.statusCode() + "\".");
            }
        } catch (NullPointerException | InterruptedException | IOException e) {
            System.out.println("Ошибка сохранения.");
        }
    }

    public String load(String key) {
        String managerStatus = null;
        HttpRequest request = HttpRequest
                .newBuilder()
                .GET()
                .uri(URI.create(url + "/load/" + key + "/?token=" + token))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                managerStatus = response.body();
            } else {
                System.out.println("Ошибка загрузки состояния.\n" + "Код ошибки - \"" + response.statusCode() + "\".");
            }
        } catch (NullPointerException | InterruptedException | IOException e) {
            System.out.println("Ошибка загрузки.");
        }
        return managerStatus;
    }
}
