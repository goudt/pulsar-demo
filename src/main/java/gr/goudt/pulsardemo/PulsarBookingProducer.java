package gr.goudt.pulsardemo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.Schema;
import org.springframework.stereotype.Component;

@Component
public class PulsarBookingProducer {
    private final PulsarClient client;
    private final Producer<String> calendarProducer;
    private final Producer<String> bookingProducer;
    private final ObjectMapper mapper = new ObjectMapper();

    public PulsarBookingProducer() throws Exception {
        // Initialize the Pulsar client and producer
        this.client = PulsarClient.builder()
                .serviceUrl("pulsar://localhost:6650") // Adjust the URL to your Pulsar broker
                .build();

        this.calendarProducer = client.newProducer(Schema.STRING)
                .topic("calendar-topic")
                .create();

        this.bookingProducer = client.newProducer(Schema.STRING)
                .topic("booking-topic")
                .create();
    }

    public void sendCalendarStatus(Bookings bookings) throws Exception {
        String message = mapper.writeValueAsString(bookings.calendar);
        calendarProducer.send(message);
    }

    public MessageId sendBookingRequest(Bookings.Request request) throws Exception {
        String message = mapper.writeValueAsString(request);
        return  bookingProducer.send(message);
    }
    public void close() throws Exception {
        calendarProducer.close();
        client.close();
    }
}