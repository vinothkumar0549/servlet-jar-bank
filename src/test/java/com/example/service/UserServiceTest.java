package com.example.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
//import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.database.Storage;
import com.example.pojo.Activity;
import com.example.pojo.User;
import com.example.util.ActivityType;
import com.example.pojo.RoleType;

class UserServiceTest {
    
    @Mock
    private Storage storage;

    @InjectMocks
    private UserService userservice;

    @BeforeEach
    void setsup(){
        MockitoAnnotations.openMocks(this);
    }


    @ParameterizedTest
    @MethodSource("provideRegisterTestCases")
    void testRegister(User user, boolean mockWriteUser, boolean mockWriteActivity, 
                      Class<? extends Exception> expectedException, String expectedMessage, int expectedUserId) {

        // Mock storage behavior
        when(storage.writeUser(any(User.class))).thenReturn(mockWriteUser);
        when(storage.writeActivity(any())).thenReturn(mockWriteActivity);

        if (expectedException != null) {
            Exception exception = assertThrows(expectedException, () -> userservice.register(user));
            assertEquals(expectedMessage, exception.getMessage());
        } else {
            int userId = userservice.register(user);
            assertTrue(userId >= 1000 && userId <= 9999, "expected user ID on successful registration");
        }
    }

    static Stream<Arguments> provideRegisterTestCases() {
        return Stream.of(
            // Test case 1: Null user should throw IllegalArgumentException
            Arguments.of(null, false, false, IllegalArgumentException.class, "User object cannot be null", 0),

            // Test case 2: Storage failure should throw RuntimeException
            Arguments.of(new User("name1", "pass1", RoleType.CUSTOMER), false, false, RuntimeException.class, "User registration failed", 0),

            // Test case 3: Activity logging failure should throw RuntimeException
            Arguments.of(new User("name2", "pass2",RoleType.ADMIN), true, false, RuntimeException.class, "Failed to log account creation activity", 0),

            // Test case 4: Successful registration should return a valid user ID
            Arguments.of(new User("name3", "pass3",RoleType.CUSTOMER), true, true, null, null, 1) // Expected user ID > 1000
        );
    }

    static Stream<Arguments> loginTestCases() {
        return Stream.of(
            Arguments.of(101, "password123", null, IllegalArgumentException.class, "User not found"), // User does not exist
            Arguments.of(102, "wrongpass", new User(1002, "Alice", "password123", RoleType.CUSTOMER, 100201002, 5000.0), SecurityException.class, "Incorrect password"), // Wrong password
            Arguments.of(103, "password123", new User(1003, "Bob", "password123", RoleType.ADMIN, 100301003, 6000.0), RuntimeException.class, "Failed to log login activity"), // Logging activity failure
            Arguments.of(104, "password123", new User(1004, "Charlie", "password123", RoleType.CUSTOMER, 100401004, 7000.0), null, null) // Successful login
        );
    }

    @ParameterizedTest
    @MethodSource("loginTestCases")
    void testLogin(int userid, String password, User user, Class<? extends Exception> expectedException, String expectedMessage) {
        when(storage.getUser(userid)).thenReturn(user);

        if (user != null) {
            when(storage.writeActivity(any(Activity.class))).thenReturn(expectedException == null);
        }

        if (expectedException != null) {
            Exception exception = assertThrows(expectedException, () -> userservice.login(userid, password));
            assertEquals(expectedMessage, exception.getMessage());
        } else {
            User loggedInUser = userservice.login(userid, password);
            assertNotNull(loggedInUser);
            assertEquals(user.getUserid(), loggedInUser.getUserid());
        }
    }

    static Stream<Arguments> withdrawTestCases() {
        return Stream.of(
            Arguments.of(null, 500, IllegalArgumentException.class, "User not found"),  // Null user
            Arguments.of(new User(101, "Alice", "password", RoleType.CUSTOMER, 1001, 5000.0), -100, IllegalArgumentException.class, "Invalid withdrawal amount"),  // Negative amount
            Arguments.of(new User(102, "Bob", "password", RoleType.CUSTOMER, 1002, 300.0), 500, IllegalStateException.class, "Insufficient balance"),  // Insufficient funds
            Arguments.of(new User(103, "Charlie", "password", RoleType.CUSTOMER, 1003, 7000.0), 1000, RuntimeException.class, "Failed to update user balance"),  // Failed user update
            Arguments.of(new User(104, "David", "password", RoleType.CUSTOMER, 1004, 8000.0), 1000, RuntimeException.class, "Failed to log withdrawal activity"),  // Failed activity logging
            Arguments.of(new User(105, "Eve", "password", RoleType.CUSTOMER, 1005, 9000.0), 1000, null, null)  // Successful withdrawal
        );
    }

    @ParameterizedTest
    @MethodSource("withdrawTestCases")
    void testWithdraw(User user, int amount, Class<? extends Exception> expectedException, String expectedMessage) {
        if (user != null) {
            when(storage.updateUser(user)).thenReturn(expectedException == null || !expectedMessage.equals("Failed to update user balance"));
            when(storage.writeActivity(any(Activity.class))).thenReturn(expectedException == null);
        }

        if (expectedException != null) {
            Exception exception = assertThrows(expectedException, () -> userservice.withdraw(user, amount));
            assertEquals(expectedMessage, exception.getMessage());
        } else {
            assertTrue(userservice.withdraw(user, amount), "Withdrawal should be successful");
        }
    }

    static Stream<Arguments> depositTestCases() {
        return Stream.of(
            Arguments.of(null, 500, IllegalArgumentException.class, "User not found"),  // Null user
            Arguments.of(new User(101, "Alice", "password", RoleType.CUSTOMER, 1001, 5000.0), -100, IllegalArgumentException.class, "Invalid deposit amount"),  // Negative deposit
            Arguments.of(new User(102, "Bob", "password", RoleType.CUSTOMER, 1002, 300.0), 0, IllegalArgumentException.class, "Invalid deposit amount"),  // Zero deposit
            Arguments.of(new User(103, "Charlie", "password", RoleType.CUSTOMER, 1003, 7000.0), 1000, RuntimeException.class, "Failed to update user balance"),  // Failed user update
            Arguments.of(new User(104, "David", "password", RoleType.CUSTOMER, 1004, 8000.0), 1500, null, null)  // Successful deposit
        );
    }

    @ParameterizedTest
    @MethodSource("depositTestCases")
    void testDeposit(User user, int amount, Class<? extends Exception> expectedException, String expectedMessage) {
        if (user != null) {
            when(storage.updateUser(user)).thenReturn(expectedException == null);
            when(storage.writeActivity(any(Activity.class))).thenReturn(true);
        }

        if (expectedException != null) {
            Exception exception = assertThrows(expectedException, () -> userservice.deposit(user, amount));
            assertEquals(expectedMessage, exception.getMessage());
        } else {
            assertTrue(userservice.deposit(user, amount), "Deposit should be successful");
        }
    }

    static Stream<Arguments> moneyTransferTestCases() {
        return Stream.of(
            Arguments.of(null, 102, 500, IllegalArgumentException.class, "Sender user not found"), // Null sender
            Arguments.of(new User(1001, "Alice", "password", RoleType.CUSTOMER, 100101001, 5000.0), 1002, -100, IllegalArgumentException.class, "Invalid transfer amount"), // Negative transfer amount
            Arguments.of(new User(1001, "Alice", "password", RoleType.CUSTOMER, 100101001, 5000.0), 1002, 0, IllegalArgumentException.class, "Invalid transfer amount"), // Zero transfer amount
            Arguments.of(new User(1001, "Alice", "password", RoleType.CUSTOMER, 100101001, 300.0), 1002, 500, IllegalStateException.class, "Insufficient balance"), // Insufficient balance
            Arguments.of(new User(1001, "Alice", "password", RoleType.CUSTOMER, 100101001, 5000.0), 1003, 500, IllegalArgumentException.class, "Receiver account not valid"), // Receiver is ADMIN
            Arguments.of(new User(1001, "Alice", "password", RoleType.CUSTOMER, 100101001, 5000.0), 1004, 500, RuntimeException.class, "Failed to update user balances"), // Failed user update
            Arguments.of(new User(1001, "Alice", "password", RoleType.CUSTOMER, 100101001, 5000.0), 1005, 500, null, null) // Successful transfer
        );
    }

    @ParameterizedTest
    @MethodSource("moneyTransferTestCases")
    void testMoneyTransfer(User user, int receiverId, int amount, Class<? extends Exception> expectedException, String expectedMessage) {
        User sender = user;
        User receiver = (receiverId == 1003)
            ? new User(1003, "Admin", "password", RoleType.ADMIN, 2003, 7000.0) // Invalid receiver (ADMIN)
            : new User(receiverId, "Receiver", "password", RoleType.CUSTOMER, 2004, 2000.0); // Valid receiver

        if (sender != null) {
            when(storage.getUser(receiverId)).thenReturn(receiver);
            when(storage.updateUser(sender)).thenReturn(expectedException == null);
            when(storage.updateUser(receiver)).thenReturn(expectedException == null);
            when(storage.writeActivity(any(Activity.class))).thenReturn(true);
        }

        if (expectedException != null) {
            Exception exception = assertThrows(expectedException, () -> userservice.moneytransfer(sender, receiverId, amount));
            assertEquals(expectedMessage, exception.getMessage());
        } else {
            assertTrue(userservice.moneytransfer(sender, receiverId, amount), "Money transfer should be successful");
        }
    }

    static Stream<Arguments> logoutTestCases() {
        return Stream.of(
            Arguments.of(null, IllegalArgumentException.class, "Invalid user for logout"), // Null user
            Arguments.of(new User(101, "Alice", "password", null, 1001, 5000.0), null, null) // Valid user
        );
    }

    @ParameterizedTest
    @MethodSource("logoutTestCases")
    void testLogout(User user, Class<? extends Exception> expectedException, String expectedMessage) {

        if (user != null) {
            when(storage.writeActivity(any(Activity.class))).thenReturn(true);
        }

        if (expectedException != null) {
            Exception exception = assertThrows(expectedException, () -> userservice.logout(user));
            assertEquals(expectedMessage, exception.getMessage());
        } else {
            assertTrue(userservice.logout(user), "Logout should be successful");
        }
    }

     static Stream<Arguments> providePrintTestCases() {
        User user = new User(1, "JohnDoe", "password123", null, 1001, 5000);

        List<Activity> activities = Arrays.asList(
                new Activity("1", 1, 1001, 0, 200, new Date(), ActivityType.DEPOSIT),
                new Activity("2", 1, 1001, 0, 100, new Date(), ActivityType.WITHDRAW)
        );

        return Stream.of(
                Arguments.of(null, null, IllegalArgumentException.class, "User cannot be null"),
                Arguments.of(user, null, RuntimeException.class, "No activity data available for the user"),
                Arguments.of(user, Collections.emptyList(), RuntimeException.class, "No activity data available for the user"),
                Arguments.of(user, activities, null, null)  // Successful case
        );
    }

    @ParameterizedTest
    @MethodSource("providePrintTestCases")
    void testPrintActivity(User user, List<Activity> mockActivities, Class<? extends Exception> expectedException, String expectedMessage) {
        when(storage.getActivity(user)).thenReturn(mockActivities);

        if (expectedException != null) {
            Exception exception = assertThrows(expectedException, () -> userservice.printActivity(user));
            assertEquals(expectedMessage, exception.getMessage());
        } else {
            List<Activity> result = userservice.printActivity(user);
            assertNotNull(result);
            assertEquals(mockActivities.size(), result.size());
        }
    }

    // @ParameterizedTest
    // @CsvSource({
    //     "3",   // Valid case: Requesting top 3 customers
    //     "1",   // Valid case: Requesting top 1 customer
    //     "5"    // Valid case: Requesting top 5 customers
    // })
    // void testGetTopNCustomer_ValidInput(int n) {
    //     List<User> mockUsers = Arrays.asList(
    //         new User(1, "Alice", "encPass1", RoleType.CUSTOMER, 101, 5000.0),
    //         new User(2, "Bob", "encPass2", RoleType.CUSTOMER, 102, 3000.0)
    //     );

    //     when(storage.TopNBalance(n)).thenReturn(mockUsers);

    //     List<User> result = userservice.getTopNCustomer(n);

    //     assertNotNull(result);
    //     assertFalse(result.isEmpty());
    //     assertEquals(mockUsers.size(), result.size());
    //     verify(storage, times(1)).TopNBalance(n);
    // }

    // @ParameterizedTest
    // @CsvSource({
    //     "0",   // Invalid case: Zero customers requested
    //     "-1"   // Invalid case: Negative number requested
    // })
    // void testGetTopNCustomer_InvalidInput_ThrowsException(int n) {
    //     Exception exception = assertThrows(IllegalArgumentException.class, () -> {
    //         userservice.getTopNCustomer(n);
    //     });

    //     assertEquals("The number of customers must be greater than zero", exception.getMessage());
    // }

    // @ParameterizedTest
    // @CsvSource("3")
    // void testGetTopNCustomer_NoDataAvailable_ThrowsException(int n) {
    //     when(storage.TopNBalance(n)).thenReturn(Collections.emptyList());

    //     Exception exception = assertThrows(RuntimeException.class, () -> {
    //         userservice.getTopNCustomer(n);
    //     });

    //     assertEquals("No customer data available", exception.getMessage());
    // }

}










// @Test
//     void testRegister_NullUser_ThrowsIllegalArgumentException() {
//         Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//             userservice.register(null);
//         });
    
//         assertEquals("User object cannot be null", exception.getMessage());
//     }

//     @Test
//     void testRegister_StorageFailure_ThrowsRuntimeException() {
//         User user = new User();
//         user.setRole(RoleType.CUSTOMER);

//         // Simulating failure in writing user data
//         when(storage.writeUser(any(User.class))).thenReturn(false);

//         Exception exception = assertThrows(RuntimeException.class, () -> {
//             userservice.register(user);
//         });

//         assertEquals("User registration failed", exception.getMessage());
//     }

//     @Test
//     void testRegister_ActivityLoggingFailure_ThrowsRuntimeException() {
//         User user = new User();
//         user.setRole(RoleType.CUSTOMER);

//         // Simulating success in writing user but failure in activity logging
//         when(storage.writeUser(any(User.class))).thenReturn(true);
//         when(storage.writeActivity(any())).thenReturn(false);

//         Exception exception = assertThrows(RuntimeException.class, () -> {
//             userservice.register(user);
//         });

//         assertEquals("Failed to log account creation activity", exception.getMessage());
//     }

//     @Test
//     void testRegister_SuccessfulRegistration() {
//         User user = new User();
//         user.setRole(RoleType.CUSTOMER);

//         // Simulating successful user and activity storage
//         when(storage.writeUser(any(User.class))).thenReturn(true);
//         when(storage.writeActivity(any())).thenReturn(true);

//         int userId = userservice.register(user);

//         assertTrue(userId > 0, "User ID should be greater than 0 on successful registration");
//     }
