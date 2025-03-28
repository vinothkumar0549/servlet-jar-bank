package com.example.pojo;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
//import com.example.pojo.RoleType;

class UserTest {

    @Test
    void testUserConstructorWithAllFields() {
        // Creating User Object with all fields
        User user = new User(101, "John Doe", "encrypted123", RoleType.CUSTOMER, 123456, 5000.0);

        // Verifying the constructor values
        assertEquals(101, user.getUserid());
        assertEquals("John Doe", user.getName());
        assertEquals("encrypted123", user.getEncryptedpassword());
        assertEquals(RoleType.CUSTOMER, user.getRole());
        assertEquals(123456, user.getAccountno());
        assertEquals(5000.0, user.getBalance());
    }

    @Test
    void testUserConstructorWithPartialFields() {
        // Creating User Object with limited fields
        User user = new User("John Doe", "encrypted123", RoleType.ADMIN);

        // Verifying the constructor values
        assertEquals("John Doe", user.getName());
        assertEquals("encrypted123", user.getEncryptedpassword());
        assertEquals(RoleType.ADMIN, user.getRole());
    }

    @Test
    void testSettersAndGetters() {
        User user = new User(null, null, null);

        // Using setters to update values
        user.setUserid(202);
        user.setName("Jane Doe");
        user.setEncryptedpassword("newpassword");
        user.setRole(RoleType.CUSTOMER);
        user.setAccountno(789012);
        user.setBalance(10000.0);

        // Verifying the updated values using getters
        assertEquals(202, user.getUserid());
        assertEquals("Jane Doe", user.getName());
        assertEquals("newpassword", user.getEncryptedpassword());
        assertEquals(RoleType.CUSTOMER, user.getRole());
        assertEquals(789012, user.getAccountno());
        assertEquals(10000.0, user.getBalance());
    }

    @Test
    void testToStringMethod() {
        User user = new User(303, "Alice", "securepwd", RoleType.CUSTOMER, 654321, 7000.0);

        String expectedOutput = String.format("%-20s%-20s%-20s%-20s%-20s%-20s%n", 
                303, "Alice", "securepwd", RoleType.CUSTOMER, 654321, 7000.0);

        assertEquals(expectedOutput, user.toString());
    }
}
