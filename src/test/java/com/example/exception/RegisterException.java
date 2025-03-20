package com.example.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.database.Storage;
import com.example.pojo.User;
import com.example.service.*;
import com.example.util.RoleType;

class RegisterException {

    @Mock
    private Storage storage;

    @InjectMocks
    private UserService userService;

    //ByteArrayOutputStream errContent;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        //errContent = new ByteArrayOutputStream();
        //System.setErr(new PrintStream(errContent)); // Redirect System.err
    }


    // @Test
    // void testRegister_NullUser_ShouldPrintError() {
        

    //     int result = userService.register(null);

    //     //System.setErr(System.err); // Reset System.err to default

    //     assertEquals(0, result); // Check method returns 0
    //     assertTrue(errContent.toString().contains("User object cannot be null")); // Verify error message
    // }

    // @Test
    // void testRegister_StorageFailure_ErrorMessagePrinted() {
    //     User user = new User();
    //     user.setRole(RoleType.CUSTOMER);

    //     when(storage.writeUser(any(User.class))).thenReturn(false);

    //     userService.register(user);
    //     assertTrue(errContent.toString().contains("Error during registration: User registration failed"),
    //                "Expected error message for storage failure was not printed");
    // }

    // @Test
    // void testRegister_ActivityLoggingFailure_ErrorMessagePrinted() {
    //     User user = new User();
    //     user.setRole(RoleType.CUSTOMER);

    //     when(storage.writeUser(any(User.class))).thenReturn(true);
    //     when(storage.writeActivity(any())).thenReturn(false);

    //     userService.register(user);
    //     assertTrue(errContent.toString().contains("Error during registration: Failed to log account creation activity"),
    //                "Expected error message for activity logging failure was not printed");
    // }

    // @Test
    // void testRegister_SuccessfulRegistration_NoErrorMessagePrinted() {
    //     User user = new User();
    //     user.setRole(RoleType.CUSTOMER);

    //     when(storage.writeUser(any(User.class))).thenReturn(true);
    //     when(storage.writeActivity(any())).thenReturn(true);

    //     userService.register(user);
    //     assertTrue(errContent.toString().trim().contains(""));
    //     //assertEquals("", errContent.toString().trim(), "No error messages should be printed on successful registration");
    // }

    // static Stream<Arguments> provideTestCases() {
    //     return Stream.of(
    //         Arguments.of(null, 0, "Error during registration: User object cannot be null"), // Null User - Expect IllegalArgumentException message
    //         Arguments.of(new User("John", "pass123", RoleType.CUSTOMER), 0, "Error during registration: User registration failed"), // Storage failure
    //         Arguments.of(new User("Jane", "securePass", RoleType.CUSTOMER), 0, "Error during registration: Failed to log account creation activity"), // Activity logging failure
    //         Arguments.of(new User("Doe", "encryptedPass", RoleType.CUSTOMER), 1, "") // Successful registration - No error
    //     );
    // }

    // @ParameterizedTest
    // @MethodSource("provideTestCases")
    // void testRegister(User user, int expectedResult, String expectedErrorMessage) {
    //     if (user != null) {
    //         when(storage.writeUser(any(User.class)))
    //             .thenReturn(!expectedErrorMessage.contains("User registration failed")); // Fail storage if needed
            
    //         when(storage.writeActivity(any(Activity.class)))
    //             .thenReturn(!expectedErrorMessage.contains("Failed to log account creation activity")); // Fail activity logging if needed
    //     }

    //     int result = userService.register(user);

    //     assertTrue(errContent.toString().contains(expectedErrorMessage), 
    //                "Expected error message: " + expectedErrorMessage);
    // }



    
    @Test
    void testRegister_NullUser_ThrowsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.register(null);
        });
    
        assertEquals("User object cannot be null", exception.getMessage());
    }

    @Test
    void testRegister_StorageFailure_ThrowsRuntimeException() {
        User user = new User();
        user.setRole(RoleType.CUSTOMER);

        // Simulating failure in writing user data
        when(storage.writeUser(any(User.class))).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.register(user);
        });

        assertEquals("User registration failed", exception.getMessage());
    }

    @Test
    void testRegister_ActivityLoggingFailure_ThrowsRuntimeException() {
        User user = new User();
        user.setRole(RoleType.CUSTOMER);

        // Simulating success in writing user but failure in activity logging
        when(storage.writeUser(any(User.class))).thenReturn(true);
        when(storage.writeActivity(any())).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.register(user);
        });

        assertEquals("Failed to log account creation activity", exception.getMessage());
    }

    @Test
    void testRegister_SuccessfulRegistration() {
        User user = new User();
        user.setRole(RoleType.CUSTOMER);

        // Simulating successful user and activity storage
        when(storage.writeUser(any(User.class))).thenReturn(true);
        when(storage.writeActivity(any())).thenReturn(true);

        int userId = userService.register(user);

        assertTrue(userId > 0, "User ID should be greater than 0 on successful registration");
    }

}
