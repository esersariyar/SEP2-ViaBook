package test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import model.User;
import model.Appointment;
import java.time.LocalDateTime;

public class SystemTest {
    @Test
    void userLoginAndAppointmentFlow() {
        // User login simulation
        User user = new User("patient", "patient@patient.com", "John", "Doe", "patient");
        user.setId(1);
        assertEquals("patient@patient.com", user.getEmail());
        assertEquals("patient", user.getPassword());
        assertEquals("patient", user.getRole());

        // Appointment creation simulation
        LocalDateTime appointmentTime = LocalDateTime.of(2025, 2, 10, 11, 0);
        Appointment appointment = new Appointment();
        appointment.setPatientId(user.getId());
        appointment.setDentistId(2);
        appointment.setAppointmentTime(appointmentTime);
        appointment.setStatus("pending");

        assertEquals(1, appointment.getPatientId());
        assertEquals(2, appointment.getDentistId());
        assertEquals(appointmentTime, appointment.getAppointmentTime());
        assertEquals("pending", appointment.getStatus());
    }
} 