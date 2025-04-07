package com.example.pojo;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import com.example.util.ActivityType;

class ActivityTest {

    @Test
    void testActivityConstructorAndGetters() {
        
        Timestamp date = Timestamp.valueOf(LocalDateTime.now());

        // Creating Activity Object
        Activity activity = new Activity("ACT123", 101, 123456, 654321, 5000.0, date, ActivityType.DEPOSIT);

        // Verifying Constructor Initialization
        assertEquals("ACT123", activity.getActivityid());
        assertEquals(101, activity.getUserid());
        assertEquals(123456, activity.getAccountfrom());
        assertEquals(654321, activity.getAccountto());
        assertEquals(5000.0, activity.getAmount());
        assertEquals(date, activity.getDate());
        assertEquals(ActivityType.DEPOSIT, activity.getActivity());
    }

    @Test
    void testSetters() {
        Activity activity = new Activity(null, 0, 0, 0, 0.0, null, null);
        Timestamp date = Timestamp.valueOf(LocalDateTime.now());

        // Setting values using setters
        activity.setActivityid("ACT456");
        activity.setUserid(202);
        activity.setAccountfrom(111111);
        activity.setAccountto(222222);
        activity.setAmount(10000.0);
        activity.setDate(date);
        activity.setActivity(ActivityType.WITHDRAW);

        // Verifying the updated values
        assertEquals("ACT456", activity.getActivityid());
        assertEquals(202, activity.getUserid());
        assertEquals(111111, activity.getAccountfrom());
        assertEquals(222222, activity.getAccountto());
        assertEquals(10000.0, activity.getAmount());
        assertEquals(date, activity.getDate());
        assertEquals(ActivityType.WITHDRAW, activity.getActivity());
    }

    @Test
    void testToStringMethod() {
        Timestamp date = Timestamp.valueOf(LocalDateTime.now());
        Activity activity = new Activity("ACT123", 101, 123456, 654321, 5000.0, date, ActivityType.DEPOSIT);

        String expectedOutput = String.format("%-45s%-20s%-20s%-20s%-20s%-30s%-20s%n", 
                "ACT123", 101, 123456, 654321, 5000.0, date, ActivityType.DEPOSIT);

        assertEquals(expectedOutput, activity.toString());
    }
}
