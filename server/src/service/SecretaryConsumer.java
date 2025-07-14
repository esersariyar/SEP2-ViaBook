package service;

import model.Appointment;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import dao.AppointmentDAO;

public class SecretaryConsumer extends Thread {
    private final BlockingQueue<Appointment> queue;
    private final Random random = new Random();

    public SecretaryConsumer(BlockingQueue<Appointment> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        AppointmentDAO appointmentDAO = new AppointmentDAO();
        try {
            while (true) {
                Appointment appointment = queue.take();
                boolean approved = random.nextBoolean();
                String newStatus = approved ? "approved" : "cancelled";
                appointment.setStatus(newStatus);
                boolean dbResult = appointmentDAO.updateAppointmentStatus(appointment.getId(), newStatus);
                if (dbResult) {
                    System.out.println("[APPOINTMENT REVIEWED] Secretary " + (approved ? "approved" : "rejected") + " appointment (ID: " + appointment.getId() + ") for Patient " + appointment.getPatientId() + " with Dentist " + appointment.getDentistId() + ".");
                } else {
                    System.out.println("[ERROR] Failed to update appointment status in database for appointment ID: " + appointment.getId());
                }
                Thread.sleep(5000 + random.nextInt(5000));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
} 