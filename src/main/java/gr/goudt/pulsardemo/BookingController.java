package gr.goudt.pulsardemo;


import lombok.RequiredArgsConstructor;
import org.apache.pulsar.client.api.MessageId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BookingController {

    private final PulsarBookingProducer producer;

    @GetMapping("/book/{day}")
    public ResponseEntity<MessageId> book(@PathVariable("day") String day) throws Exception {
        MessageId messageId = producer.sendBookingRequest(new Bookings.Request(Bookings.DAYS.valueOf(day.toUpperCase()),
                Bookings.STATUS.BOOKED));

        return ResponseEntity.ok(messageId);
    }

    @GetMapping("/cancel/{day}")
    public ResponseEntity<MessageId> cancel(@PathVariable("day") String day) throws Exception {
        MessageId messageId = producer.sendBookingRequest(new Bookings.Request(Bookings.DAYS.valueOf(day.toUpperCase()),
                Bookings.STATUS.OPEN));

        return ResponseEntity.ok(messageId);
    }

}

