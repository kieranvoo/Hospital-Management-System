import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * The Appointment class represents an appointment in the hospital management system.
 * It includes details about the patient, doctor, date and time, status, prescribed medications,
 * and consultation notes.
 */
public class Appointment implements Serializable{
    private static final long serialVersionUID = 1L;
    /** The unique identifier for the appointment. */
    private String appointmentID;
    /** The identifier of the patient associated with this appointment. */
    private String patientID;
    /** The identifier of the doctor assigned to this appointment. */
    private String doctorID;
    /** The date and time of the appointment. */
    private LocalDateTime dateTime;
     /** The current status of the appointment (e.g., "Pending", "Confirmed", "Completed"). */
    private String status;
    /** A map of prescribed medications and their quantities. */
    private Map<Medication, Integer> prescribedMedication;
    /** The current medication status for the appointment (e.g., "Pending to Dispense", "Dispense Complete"). */
    private String medicationStatus;
    /** The consultation notes recorded during the appointment. */
    private String consultationNotes;
    private static Scanner sc = new Scanner(System.in);

    /**
     * Constructor for Appointment.
     *
     * @param appointmentID      The unique ID of the appointment.
     * @param patientID          The ID of the patient.
     * @param doctorID           The ID of the doctor.
     * @param dateTime           The date and time of the appointment.
     * @param comments           Initial comments or notes for the appointment.
     */
    public Appointment(String appointmentID, String patientID, String doctorID, LocalDateTime dateTime, String comments) {
        this.appointmentID = appointmentID;
        this.patientID = patientID;
        this.doctorID = doctorID;
        this.dateTime = dateTime;
        this.status = "Pending";
        this.prescribedMedication = new HashMap<>();
        this.medicationStatus = null;
        this.consultationNotes = null;
        //if (comments != null && !comments.isEmpty()) {
            //this.consultationNotes = comments; // Use consultationNotes for initial comments
        //}
    }
    
    /**
 * Retrieves the unique identifier of the appointment.
 * This ID is used to uniquely distinguish each appointment in the system.
 *
 * @return The unique ID of the appointment as a {@code String}.
 */
    public String getAppointmentID() {
        return appointmentID;
    }

    /**
 * Retrieves the unique ID of the doctor assigned to this appointment.
 *
 * @return The unique doctor ID associated with this appointment.
 */
    public String getDoctorID() {
        return doctorID;
    }

    /**
 * Retrieves the unique ID of the patient associated with this appointment.
 *
 * @return The unique patient ID associated with this appointment.
 */
    public String getPatientID(){
        return patientID;
    }

     /**
 * Retrieves the scheduled date and time of the appointment.
 *
 * @return The date and time of the appointment as a {@code LocalDateTime}.
 */
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    /**
 * Retrieves the medication status of the appointment.
 * The medication status indicates whether medication is pending, dispensed, or not prescribed.
 *
 * @return The medication status as a {@code String}.
 */
    public String getMedicationStatus() {
        return medicationStatus;
    }

    /**
 * Retrieves a map of medications prescribed during the appointment along with their quantities.
 *
 * @return A map containing {@code Medication} objects as keys and their respective quantities as values.
 */
    public Map<Medication, Integer> getPrescribedMedication() {
        return this.prescribedMedication;
    }

    /**
 * Retrieves the consultation notes recorded during the appointment.
 *
 * @return The consultation notes as a {@code String}, or {@code null} if no notes are recorded.
 */
    public String getConsultationNotes(){
        return consultationNotes;
    }
    
    /** 
     * Confirms the appointment if it is in a "Pending" state.
     */
    public void confirm() {
        if (!status.equals("Pending")) {
            System.out.println("Only pending appointments can be confirmed.");
            return;
        }
        this.status = "Confirmed";
    }
    
    /** 
     * Cancels the appointment unless it is already completed or canceled.
     */
    public void cancel() {
        if (status.equals("Completed")) {
            System.out.println("Completed appointments cannot be canceled.");
            return;
        }
        if (status.equals("Cancelled")) {
            System.out.println("Appointment is already canceled.");
            return;
        }
        this.status = "Cancelled";
    }
    
    /**
 * Marks the appointment as completed by recording prescribed medications and consultation notes.
 * Prescribed medications are added to the appointment, and their quantities are specified.
 *
 * @param inventory      The {@code Inventory} instance to fetch and validate medication details.
 * @param appointmentID  The unique ID of the appointment being completed.
 */
    public void complete(Inventory inventory, String appointmentID) {
        if (!status.equals("Confirmed")) {
            System.out.println("Only confirmed appointments can be completed.");
            return;
        }

        System.out.println("""
                Prescribe Medication for Appointment (if any): 
                1. Yes
                2. No
                Choose options (1-2): """);
        int option = sc.nextInt();
    
        switch (option) {
            case 1:
                inventory.displayInventory(false);
                System.out.println("Medication ID: ");
                String medicationID = sc.next();
                System.out.println("Quantity: ");
                int quantity = sc.nextInt();
                Medication medication = inventory.findMedicationByID(medicationID);
                if (medication == null) {
                    System.out.println("Medication Not Found. Please Try Again.");
                    return;
                }
                this.prescribedMedication.put(medication, quantity);
                this.medicationStatus = "Pending to Dispense";
                break;
            case 2:
                break;
            default:
                System.out.println("Invalid option. Please try again.");
                break;
        }
    
        System.out.println("Consultation Notes for Appointment (if any): ");
        sc.nextLine();
        this.consultationNotes = sc.nextLine();
        this.status = "Completed";
        System.out.println("Outcome recorded successfully for Appointment ID: " + appointmentID);
    }
    
    /** 
     * Marks the medication as dispensed.
     */
    public void completeDispense() {
        this.medicationStatus = "Dispense Complete";
    }

    /**
 * Retrieves the current status of the appointment.
 * The status could be one of the following: "Pending", "Confirmed", "Completed", or "Cancelled".
 *
 * @return The current status of the appointment as a {@code String}.
 */
    public String getStatus() {
        return status;
    }

    /**
 * Updates the scheduled date and time of the appointment.
 * This action is only allowed if the appointment is in "Pending" or "Confirmed" status.
 *
 * @param newDateTime The new date and time for the appointment as a {@code LocalDateTime}.
 */
    public void updateDateTime(LocalDateTime newDateTime) {
        if (!status.equals("Pending") && !status.equals("Confirmed")) {
            System.out.println("Only pending or confirmed appointments can be rescheduled.");
            return;
        }
        System.out.println("Updating appointment date and time from " + this.dateTime + " to " + newDateTime);
        this.dateTime = newDateTime;
    }
    
    /**
 * Provides a string representation of the appointment.
 * Includes details such as appointment ID, patient ID, doctor ID, date and time, status, 
 * medication status, and consultation notes.
 *
 * @return A string summarizing the appointment details.
 */
    @Override
    public String toString() {
        return "Appointment [Appointment ID=" + appointmentID 
            + ", Patient ID=" + patientID 
            + ", Doctor ID=" + doctorID
            + ", Date and Time=" + dateTime 
            + ", Status=" + status 
            + (medicationStatus != null ? ", Medication Status=" + medicationStatus : "")
            + (consultationNotes != null ? ", Consultation Notes=" + consultationNotes : "")
            + "]";
    }
}
