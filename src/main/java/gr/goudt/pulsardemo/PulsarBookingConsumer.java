package gr.goudt.pulsardemo;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.SubscriptionInitialPosition;
import org.apache.pulsar.client.api.SubscriptionType;
import org.springframework.pulsar.annotation.PulsarListener;
import org.springframework.pulsar.annotation.PulsarReader;
import org.springframework.stereotype.Component;

@Component
@Slf4j

public class PulsarBookingConsumer {

    private final PulsarClient client;
    private final Consumer<String> calendarConsumer;
    private final Consumer<String> bookingConsumer;
    private final ObjectMapper mapper = new ObjectMapper();


    private final Bookings bookings;

    private final PulsarBookingProducer producer;

    public PulsarBookingConsumer(Bookings bookings, PulsarBookingProducer producer) throws Exception {
        this.bookings = bookings;
        this.producer = producer;
        this.client = PulsarClient.builder()
                .serviceUrl("pulsar://localhost:6650") // Adjust the URL to your Pulsar broker
                .build();

        this.calendarConsumer = client.newConsumer(Schema.STRING)
                .topic("calendar-topic")
                .subscriptionName("calendar-subscription")
                .subscriptionType(SubscriptionType.Shared)
                .subscriptionInitialPosition(SubscriptionInitialPosition.Earliest)
                .subscribe();

        this.bookingConsumer = client.newConsumer(Schema.STRING)
                .topic("booking-topic")
                .subscriptionName("booking-subscription")
                .subscriptionType(SubscriptionType.Exclusive)
                .subscriptionInitialPosition(SubscriptionInitialPosition.Earliest)
                .subscribe();
    }


    @PulsarReader(topics = {"calendar-topic"})
    public void consumeCalendarMessages(Message<String> msg) {
        log.info("Calendar: " + bookings.calendar);
    }

    @PulsarListener(topics = {"booking-topic"})
    public void consumeBookingMessages(Message<String> msg) throws Exception {
        try {
            Bookings.Request request = mapper.readValue(msg.getValue(), Bookings.Request.class);

            log.info("Received message: " + request);
            bookings.calendar.put(request.day(), request.status());
            producer.sendCalendarStatus(bookings);

            // Acknowledge the message
            bookingConsumer.acknowledge(msg);
        } catch (Exception e) {
            log.error("Failed to process message: " + e.getMessage());
            bookingConsumer.negativeAcknowledge(msg);

        }
    }

    public void close() throws Exception {
        calendarConsumer.close();
        bookingConsumer.close();
        client.close();
    }
}
