package test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import model.Appointment;
import java.time.LocalDateTime;

public class AppointmentTest {
    private Appointment appointment;
    private LocalDateTime testTime;

    @BeforeEach
    void setUp() {
        appointment = new Appointment();
        testTime = LocalDateTime.of(2025, 1, 15, 10, 30);
    }

    @Test
    void testDefaultConstructor() {
        Appointment newAppointment = new Appointment();
        assertEquals(0, newAppointment.getId());
        assertEquals(0, newAppointment.getPatientId());
        assertEquals(0, newAppointment.getDentistId());
        assertNull(newAppointment.getAppointmentTime());
        assertNull(newAppointment.getStatus());
        assertNull(newAppointment.getCreatedAt());
    }

    @Test
    void testParameterizedConstructor() {
        Appointment newAppointment = new Appointment(1, 2, testTime);
        assertEquals(1, newAppointment.getPatientId());
        assertEquals(2, newAppointment.getDentistId());
        assertEquals(testTime, newAppointment.getAppointmentTime());
        assertEquals("pending", newAppointment.getStatus());
        assertEquals(0, newAppointment.getId());
        assertNull(newAppointment.getCreatedAt());
    }

    @Test
    void testSettersAndGetters() {
        appointment.setId(1);
        appointment.setPatientId(2);
        appointment.setDentistId(3);
        appointment.setAppointmentTime(testTime);
        appointment.setStatus("confirmed");
        assertEquals(1, appointment.getId());
        assertEquals(2, appointment.getPatientId());
        assertEquals(3, appointment.getDentistId());
        assertEquals(testTime, appointment.getAppointmentTime());
        assertEquals("confirmed", appointment.getStatus());
    }

    @Test
    void testPatientDentistAppointment() {
        appointment.setPatientId(1);
        appointment.setDentistId(2);
        appointment.setAppointmentTime(testTime);
        appointment.setStatus("pending");
        assertEquals(1, appointment.getPatientId());
        assertEquals(2, appointment.getDentistId());
        assertEquals(testTime, appointment.getAppointmentTime());
        assertEquals("pending", appointment.getStatus());
    }

    @Test
    void testAppointmentStatusFlow() {
        appointment.setStatus("pending");
        assertEquals("pending", appointment.getStatus());
        appointment.setStatus("confirmed");
        assertEquals("confirmed", appointment.getStatus());
        appointment.setStatus("completed");
        assertEquals("completed", appointment.getStatus());
        appointment.setStatus("cancelled");
        assertEquals("cancelled", appointment.getStatus());
    }

    @Test
    void testAppointmentTimeVariations() {
        LocalDateTime morningTime = LocalDateTime.of(2025, 1, 15, 9, 0);
        LocalDateTime afternoonTime = LocalDateTime.of(2025, 1, 15, 14, 30);
        LocalDateTime eveningTime = LocalDateTime.of(2025, 1, 15, 18, 0);
        appointment.setAppointmentTime(morningTime);
        assertEquals(morningTime, appointment.getAppointmentTime());
        appointment.setAppointmentTime(afternoonTime);
        assertEquals(afternoonTime, appointment.getAppointmentTime());
        appointment.setAppointmentTime(eveningTime);
        assertEquals(eveningTime, appointment.getAppointmentTime());
    }

    @Test
    void testToString() {
        appointment.setId(1);
        appointment.setPatientId(2);
        appointment.setDentistId(3);
        appointment.setAppointmentTime(testTime);
        appointment.setStatus("confirmed");
        String result = appointment.toString();
        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("patientId=2"));
        assertTrue(result.contains("dentistId=3"));
        assertTrue(result.contains("status='confirmed'"));
        assertTrue(result.contains(testTime.toString()));
    }

    @Test
    void testNullValues() {
        appointment.setAppointmentTime(null);
        appointment.setStatus(null);
        appointment.setCreatedAt(null);
        assertNull(appointment.getAppointmentTime());
        assertNull(appointment.getStatus());
        assertNull(appointment.getCreatedAt());
    }

    @Test
    void testCreatedAtTimestamp() {
        LocalDateTime createdTime = LocalDateTime.now();
        appointment.setCreatedAt(createdTime);
        assertEquals(createdTime, appointment.getCreatedAt());
    }

    @Test
    void testFutureAppointment() {
        LocalDateTime futureTime = LocalDateTime.now().plusDays(7);
        appointment.setAppointmentTime(futureTime);
        assertEquals(futureTime, appointment.getAppointmentTime());
        assertTrue(appointment.getAppointmentTime().isAfter(LocalDateTime.now()));
    }

    @Test
    void testSerializableInterface() {
        assertTrue(java.io.Serializable.class.isAssignableFrom(Appointment.class));
    }
} 