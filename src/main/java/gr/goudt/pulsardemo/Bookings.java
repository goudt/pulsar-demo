package gr.goudt.pulsardemo;

import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
public class Bookings {

public enum DAYS {MON, TUE,WED,THU,FRI,SAT,SUN}
public enum STATUS {OPEN, BOOKED}

public record Request(DAYS day, STATUS status){}

public Map<DAYS, STATUS> calendar = new EnumMap<>(DAYS.class);

    public Bookings() {
        for (DAYS day : DAYS.values()) {
            calendar.put(day, STATUS.OPEN);
        }
    }
}
