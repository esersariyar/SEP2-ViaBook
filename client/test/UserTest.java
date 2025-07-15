package test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import model.User;
import java.sql.Timestamp;

public class UserTest {
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void testDefaultConstructor() {
        User newUser = new User();
        assertEquals(0, newUser.getId());
        assertNull(newUser.getPassword());
        assertNull(newUser.getEmail());
        assertNull(newUser.getFirstName());
        assertNull(newUser.getLastName());
        assertNull(newUser.getRole());
        assertNull(newUser.getCreatedAt());
    }

    @Test
    void testParameterizedConstructor() {
        User newUser = new User("patient", "patient@patient.com", "John", "Doe", "patient");
        assertEquals("patient", newUser.getPassword());
        assertEquals("patient@patient.com", newUser.getEmail());
        assertEquals("John", newUser.getFirstName());
        assertEquals("Doe", newUser.getLastName());
        assertEquals("patient", newUser.getRole());
        assertEquals(0, newUser.getId());
        assertNull(newUser.getCreatedAt());
    }

    @Test
    void testSettersAndGetters() {
        user.setId(1);
        user.setPassword("testPass");
        user.setEmail("test@test.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRole("patient");
        assertEquals(1, user.getId());
        assertEquals("testPass", user.getPassword());
        assertEquals("test@test.com", user.getEmail());
        assertEquals("Test", user.getFirstName());
        assertEquals("User", user.getLastName());
        assertEquals("patient", user.getRole());
    }

    @Test
    void testValidPatientUser() {
        User patient = new User("patient", "patient@patient.com", "John", "Doe", "patient");
        patient.setId(1);
        assertEquals(1, patient.getId());
        assertEquals("patient", patient.getPassword());
        assertEquals("patient@patient.com", patient.getEmail());
        assertEquals("John", patient.getFirstName());
        assertEquals("Doe", patient.getLastName());
        assertEquals("patient", patient.getRole());
    }

    @Test
    void testValidDentistUser() {
        User dentist = new User("dentist", "dentist@dentist.com", "Dr. Jane", "Smith", "dentist");
        dentist.setId(2);
        assertEquals(2, dentist.getId());
        assertEquals("dentist", dentist.getPassword());
        assertEquals("dentist@dentist.com", dentist.getEmail());
        assertEquals("Dr. Jane", dentist.getFirstName());
        assertEquals("Smith", dentist.getLastName());
        assertEquals("dentist", dentist.getRole());
    }

    @Test
    void testValidSecretaryUser() {
        User secretary = new User("secretary", "secretary@secretary.com", "Alice", "Johnson", "secretary");
        secretary.setId(3);
        assertEquals(3, secretary.getId());
        assertEquals("secretary", secretary.getPassword());
        assertEquals("secretary@secretary.com", secretary.getEmail());
        assertEquals("Alice", secretary.getFirstName());
        assertEquals("Johnson", secretary.getLastName());
        assertEquals("secretary", secretary.getRole());
    }

    @Test
    void testToString() {
        user.setId(1);
        user.setEmail("test@test.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRole("patient");
        String result = user.toString();
        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("email='test@test.com'"));
        assertTrue(result.contains("firstName='Test'"));
        assertTrue(result.contains("lastName='User'"));
        assertTrue(result.contains("role='patient'"));
    }

    @Test
    void testNullValues() {
        user.setPassword(null);
        user.setEmail(null);
        user.setFirstName(null);
        user.setLastName(null);
        user.setRole(null);
        assertNull(user.getPassword());
        assertNull(user.getEmail());
        assertNull(user.getFirstName());
        assertNull(user.getLastName());
        assertNull(user.getRole());
    }

    @Test
    void testEmptyValues() {
        user.setPassword("");
        user.setEmail("");
        user.setFirstName("");
        user.setLastName("");
        user.setRole("");
        assertEquals("", user.getPassword());
        assertEquals("", user.getEmail());
        assertEquals("", user.getFirstName());
        assertEquals("", user.getLastName());
        assertEquals("", user.getRole());
    }

    @Test
    void testCreatedAtTimestamp() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        user.setCreatedAt(timestamp);
        assertEquals(timestamp, user.getCreatedAt());
    }
} 