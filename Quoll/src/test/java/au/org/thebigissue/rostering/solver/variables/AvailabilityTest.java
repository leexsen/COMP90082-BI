package au.org.thebigissue.rostering.solver.variables;

import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class AvailabilityTest {

    Availability availability=new Availability();

    @BeforeEach
    void setup(){
        //available from 9 am till 11 am
        availability.update(DayOfWeek.FRIDAY, LocalTime.of(9,0),LocalTime.of(11,0));
    }

    //make the argument as DateTime maybe a better idea

    @Test
    void updateTC1() {

        boolean re=availability.isUnavailable(LocalDateTime.of(2019, Month.of(10),25,9,0));
        assertFalse(re);
    }


    @Test
    void updateTC2() {

        boolean re=availability.isUnavailable(LocalDateTime.of(2019, Month.of(10),25,8,59));
        assertTrue(re);
    }


    @Test
    void updateTC3() {

        boolean re=availability.isUnavailable(LocalDateTime.of(2019, Month.of(10),25,11,01));
        assertTrue(re);
    }

    @Test
    void isUnavailable() {

        boolean re=availability.isUnavailable(LocalDateTime.of(2019, Month.of(10),25,11,01));
        assertTrue(re);

    }


}