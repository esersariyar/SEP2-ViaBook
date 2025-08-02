package service;

import model.Appointment;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import dao.AppointmentDAO;
import dao.UserDAO;
import model.User;
import java.util.List;

public class AppointmentProducer extends Thread {
    private final BlockingQueue<Appointment> queue;
    private final Random random = new Random();
    private final int dentistCount;
    private final int patientCount;

    public AppointmentProducer(BlockingQueue<Appointment> queue, int dentistCount, int patientCount) {
        this.queue = queue;
        this.dentistCount = dentistCount;
        this.patientCount = patientCount;
    }

    @Override
    public void run() {
        AppointmentDAO appointmentDAO = new AppointmentDAO();
        UserDAO userDAO = new UserDAO();
        List<User> patients = userDAO.getUsersByRole("patient");
        List<User> dentists = userDAO.getUsersByRole("dentist");
        if (patients.isEmpty() || dentists.isEmpty()) {
            System.out.println("[ERROR] No patients or dentists found in the database. Cannot produce appointments.");
            return;
        }
        try {
            while (true) {
                User patient = patients.get(random.nextInt(patients.size()));
                User dentist = dentists.get(random.nextInt(dentists.size()));
                // Generate appointment time in 30-minute intervals
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime baseTime = now.plusDays(random.nextInt(30)).withHour(8).withMinute(0).withSecond(0).withNano(0);
                
                // Random 30-minute slot between 8:00 and 16:00 (8 hours = 16 slots)
                int slotIndex = random.nextInt(16);
                LocalDateTime appointmentTime = baseTime.plusMinutes(slotIndex * 30);
                Appointment appointment = new Appointment(patient.getId(), dentist.getId(), appointmentTime);
                boolean dbResult = appointmentDAO.createAppointment(appointment);
                if (dbResult) {
                    queue.put(appointment);
                    System.out.println("[NEW APPOINTMENT] Patient " + patient.getFirstName() + " (ID: " + patient.getId() + ") requested an appointment with Dentist " + dentist.getFirstName() + " (ID: " + dentist.getId() + ") at " + appointmentTime + ". Status: pending");
                } else {
                    System.out.println("[ERROR] Failed to create appointment in database for Patient " + patient.getId() + ".");
                }
                Thread.sleep(5000 + random.nextInt(5000));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
} 