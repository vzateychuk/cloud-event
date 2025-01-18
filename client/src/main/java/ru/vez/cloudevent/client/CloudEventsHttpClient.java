package ru.vez.cloudevent.client;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.http.HttpMessageFactory;
import io.cloudevents.http.impl.HttpMessageWriter;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.OffsetDateTime;
import java.util.UUID;

public class CloudEventsHttpClient {

    public static void main(String[] args) throws Exception {
        // Create a CloudEvent
        CloudEvent event = CloudEventBuilder.v1()
                .withId(UUID.randomUUID().toString())
                .withSource(URI.create("https://example.com/event-source"))
                .withType("com.example.event")
                .withTime(OffsetDateTime.now())
                .withData("application/json", "{\"message\":\"Hello, CloudEvents!\"}".getBytes())
                .build();

        System.out.println("Sending CloudEvent: " + event);

        // Send the CloudEvent to the server
        sendCloudEvent(event, "http://localhost:8080/receiver");
    }

    public static void sendCloudEvent(CloudEvent event, String targetUrl) throws Exception {
        HttpClient httpClient = HttpClient.newHttpClient();

        // Prepare an HttpRequest.Builder
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(targetUrl))
                .header("Content-Type", "application/cloudevents+json");

        // Serialize the event as a structured message

        // Create the writer with appropriate lambda expressions
        HttpMessageWriter writer = HttpMessageFactory.createWriter(
                requestBuilder::header,
                body -> requestBuilder.POST(HttpRequest.BodyPublishers.ofByteArray(body))
        );
        // Serialize the event as a structured message
        writer.writeStructured(event, "application/cloudevents+json");


        // Write the CloudEvent to the request body
        HttpRequest request = requestBuilder.build();

        // Send the request
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Response Status: " + response.statusCode());
        System.out.println("Response Body: " + response.body());
    }
}
