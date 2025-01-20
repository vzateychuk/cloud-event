package ru.vez.cloudevent.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.CloudEventUtils;
import io.cloudevents.core.data.PojoCloudEventData;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.http.HttpMessageFactory;
import io.cloudevents.jackson.PojoCloudEventDataMapper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CloudEventsHttpServlet extends HttpServlet {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {

            // Read the request body into a byte array
            byte[] body = req.getInputStream().readAllBytes();

            // Log the content type and spec version
            System.out.println("Content-Type: " + req.getContentType());

            Consumer<BiConsumer<String, String>> forEachHeader = processHeader -> {
                Enumeration<String> headerNames = req.getHeaderNames();
                while (headerNames.hasMoreElements()) {
                    String name = headerNames.nextElement();
                    processHeader.accept(name, req.getHeader(name));
                }
            };

            // Parse incoming HTTP request into a CloudEvent
            MessageReader reader = HttpMessageFactory.createReader(forEachHeader, body);
            CloudEvent event = reader.toEvent();

            // Process the received event
            System.out.println("Received CloudEvent:");
            System.out.println("ID: " + event.getId());
            System.out.println("Source: " + event.getSource());
            System.out.println("Type: " + event.getType());
            System.out.println("Data: " + new String(event.getData().toBytes()));

            PojoCloudEventData<MyMessage> eventData = CloudEventUtils.mapData(
                    event,
                    PojoCloudEventDataMapper.from(objectMapper, MyMessage.class)
            );
            assert eventData != null;
            MyMessage myMessage = eventData.getValue();

            // Send a success response
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("Received message: " + myMessage.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
