import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Represents a patient in the hospital management system.
 * This class handles personal information, medical records, and appointment-related actions.
 */
public class Patient extends User {
    /** The email address of the patient. */
    private String email;
    /** The contact number of the patient. */
    private String contactNumber;
     /** The date of birth of the patient (format: yyyy-MM-dd). */
    private String dateOfBirth;
     /** The blood type of the patient. */
    private String bloodType;
     /** The list of appointments associated with the patient. */
    private List<Appointment> appointments;
    /** The medical record of the patient. */
    private MedicalRecord medicalRecord;
    /** A static scanner for user input. */
    private static Scanner sc = new Scanner(System.in);

     /**
     * Constructs a Patient instance with the specified details.
     *
     * @param patientID   The unique ID of the patient.
     * @param name        The name of the patient.
     * @param gender      The gender of the patient.
     * @param dateOfBirth The date of birth of the patient (format: yyyy-MM-dd).
     * @param email       The email address of the patient.
     * @param bloodType   The blood type of the patient.
     */
    public Patient(String patientID, String name, String gender, String dateOfBirth, String email, String bloodType) {
        super(patientID, null, name, gender, "Patient");
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.contactNumber = "Unknown";
        this.medicalRecord = new MedicalRecord(patientID, bloodType);
        this.appointments = new ArrayList<>();
    }

    /**
     * Retrieves the list of appointments for the patient.
     *
     * @return A list of the patient's appointments.
     */
    public List<Appointment> getAppointments() {
        return appointments;
    }

    /**
     * Retrieves the patient's medical record.
     *
     * @return The medical record associated with the patient.
     */
    public MedicalRecord getMedicalRecord() {
        return medicalRecord;
    }

    /**
     * Displays the patient's medical record or personal information based on user selection.
     */
    public void viewPatientInformation() {
        try {
            System.out.println("""
                    Which information would you like to view?
                    1. Medical Record
                    2. Personal Information
                    Choose options (1-2): """);
            int option = sc.nextInt();

            switch (option) {
                case 1:
                    System.out.println(medicalRecord);
                    break;
                case 2:
                    viewPersonalInformation();
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    return;
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            sc.nextLine(); // Clear the invalid input from the scanner buffer
        }
        
    }

    /**
     * Displays the personal information of the patient.
     */
    public void viewPersonalInformation() {
        System.out.println("Personal Information for Patient ID: " + userID);
        System.out.println("Patient Name: " + name);
        System.out.println("Date of Birth: " + dateOfBirth);
        System.out.println("Gender: " + gender);
        System.out.println("Contact Number: " + contactNumber);
        System.out.println("Email Address: " + email);
        System.out.println();
    }

    /**
     * Updates the patient's contact information, such as email or contact number.
     */
    public void updateContactInfo() {
        try {
            System.out.println("""
                    Update Contact Info: 
                    1. Email
                    2. Contact Number
                    Choose option (1-2) to update:  """);
            int option = sc.nextInt();

            switch (option) {
                case 1:
                    System.out.println("Enter new email: ");
                    this.email = sc.next();
                    break;
                case 2:
                    System.out.println("Enter new contact number: ");
                    this.contactNumber = sc.next();
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    return;
            }
            System.out.println("Contact information updated successfully.");
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            sc.nextLine(); // Clear the invalid input from the scanner buffer
        }
        
    }

    /**
     * Allows the patient to schedule a new appointment.
     *
     * @param schedulingSystem The scheduling system used to manage appointments.
     */
    public void scheduleAppointment(SchedulingSystem schedulingSystem) {
        try {
            // Ask for specialty
            System.out.println("Select specialty:\n1. Cardiology\n2. Psychiatry\n3. Radiology\n4. Infectious Diseases");
            int specialtyOption = sc.nextInt();
            sc.nextLine(); // Clear buffer
    
            String specialty = switch (specialtyOption) {
                case 1 -> "Cardiology";
                case 2 -> "Psychiatry";
                case 3 -> "Radiology";
                case 4 -> "Infectious Diseases";
                default -> "Invalid";
                };

    
            List<Doctor> doctors = schedulingSystem.getDoctorsBySpecialty(specialty);
            if (doctors.isEmpty()) {
                System.out.println("No doctors available for the selected specialty.");
                return;
            }
    
            // Display doctors and prompt for selection
            System.out.println("Select a doctor:");
            for (int i = 0; i < doctors.size(); i++) {
                System.out.println((i + 1) + ". Dr. " + doctors.get(i).getName());
            }
    
            int doctorIndex = sc.nextInt() - 1;
            sc.nextLine(); // Clear buffer
    
            if (doctorIndex < 0 || doctorIndex >= doctors.size()) {
                System.out.println("Invalid selection. Returning to menu.");
                return;
            }
    
            Doctor selectedDoctor = doctors.get(doctorIndex);
    
            // Display available slots
            boolean hasSlots = schedulingSystem.displayAvailableSlotsForDoctor(selectedDoctor);
            if (!hasSlots) {
                System.out.println("No available slots for the selected doctor. Returning to menu.");
                return; // Exit if no slots are available
            }
    
            // Ask for the preferred time slot
            System.out.println("Enter your preferred date (yyyy-MM-dd):");
            LocalDate preferredDate = LocalDate.parse(sc.nextLine());
    
            System.out.println("Enter your preferred time (HH:mm):");
            LocalTime preferredTime = LocalTime.parse(sc.nextLine());
    
            LocalDateTime slot = LocalDateTime.of(preferredDate, preferredTime);
    
            schedulingSystem.bookSlot(this, selectedDoctor, slot, null);
    
        } catch (Exception e) {
            System.out.println("Error scheduling appointment: " + e.getMessage());
        }
    }
    
    /**
     * Cancels an existing confirmed appointment.
     *
     * @param schedulingSystem The scheduling system used to manage appointments.
     */
    public void cancelAppointment(SchedulingSystem schedulingSystem) {
        Appointment appointmentToCancel = selectConfirmedAppointment("cancel", schedulingSystem);
        if (appointmentToCancel == null) {
            return; // If no valid appointment is selected, exit
        }
        schedulingSystem.cancelAppointment(appointmentToCancel);
    }
    
    /**
     * Reschedules an existing confirmed appointment by selecting a new date and time slot.
     *
     * @param schedulingSystem The scheduling system used to manage appointments.
     */
    public void rescheduleAppointment(SchedulingSystem schedulingSystem) {
        // Step 1: Select an appointment to reschedule
        Appointment appointmentToReschedule = selectConfirmedAppointment("reschedule", schedulingSystem);
        if (appointmentToReschedule == null) {
            return; // If no valid appointment is selected, exit
        }
    
        Doctor doctor = schedulingSystem.getDoctorById(appointmentToReschedule.getDoctorID());
        if (doctor == null) {
            System.out.println("Doctor not found. Cannot proceed with rescheduling.");
            return;
        }
    
        System.out.println("Selected appointment to reschedule:");
        System.out.printf("Date: %s, Time: %s, Patient: %s, Doctor: Dr. %s, Appointment ID: %s%n",
            appointmentToReschedule.getDateTime().toLocalDate(),
            appointmentToReschedule.getDateTime().toLocalTime(),
            appointmentToReschedule.getPatientID(),
            doctor.getName(),
            appointmentToReschedule.getAppointmentID());
    
        // Step 2: Display available slots for the doctor
        boolean hasSlots = schedulingSystem.displayAvailableSlotsForDoctor(doctor);
        if (!hasSlots) {
            System.out.println("No available slots for Dr. " + doctor.getName() + ". Returning to menu.");
            return;
        }
    
        // Step 3: Let the user choose a new date and time
        System.out.println("Enter your preferred date (yyyy-MM-dd):");
        LocalDate preferredDate = LocalDate.parse(sc.nextLine());
    
        System.out.println("Enter your preferred time (HH:mm):");
        LocalTime preferredTime = LocalTime.parse(sc.nextLine());
    
        LocalDateTime newSlot = LocalDateTime.of(preferredDate, preferredTime);
    
        // Step 4: Check if the new slot is available and reschedule
        Appointment newAppointment = schedulingSystem.bookSlot(
            schedulingSystem.getPatientById(appointmentToReschedule.getPatientID()),
            doctor,
            newSlot,
            "Rescheduled"
        );
    
        if (newAppointment != null) {
            // Cancel the old appointment
            schedulingSystem.cancelAppointment(appointmentToReschedule);
            System.out.println("Your old appointment has been canceled.");
            System.out.printf("A new request for an appointment on %s at %s has been made with Dr. %s.%n",
                newSlot.toLocalDate(), newSlot.toLocalTime(), doctor.getName());
        } else {
            System.out.println("Unable to reschedule. Would you like to cancel the appointment instead?");
            System.out.println("1: Yes\n2: No");
            int choice = sc.nextInt();
            sc.nextLine(); // Clear the buffer
    
            if (choice == 1) {
                schedulingSystem.cancelAppointment(appointmentToReschedule);
                System.out.println("Your appointment has been canceled.");
            } else {
                System.out.println("No changes were made to your appointment.");
            }
        }
    }
    
    /**
     * Selects a confirmed appointment for an action (e.g., cancel or reschedule).
     *
     * @param action            The action to perform on the appointment (e.g., "cancel" or "reschedule").
     * @param schedulingSystem  The scheduling system used to manage appointments.
     * @return The selected appointment, or null if no valid appointment is chosen.
     */
    private Appointment selectConfirmedAppointment(String action, SchedulingSystem schedulingSystem) {
        LocalDateTime now = LocalDateTime.now(); // Current time
        List<Appointment> confirmedAppointments = new ArrayList<>();
    
        // Filter for future confirmed appointments
        for (Appointment appointment : appointments) {
            if (appointment.getDateTime().isAfter(now) && "Confirmed".equals(appointment.getStatus())) {
                confirmedAppointments.add(appointment);
            }
        }
    
        if (confirmedAppointments.isEmpty()) {
            System.out.println("You have no confirmed appointments to " + action + ".");
            return null;
        }
    
        // Display confirmed appointments
        System.out.println("\nYour Confirmed Appointments:");
        for (int i = 0; i < confirmedAppointments.size(); i++) {
            Appointment appointment = confirmedAppointments.get(i);
            Doctor doctor = schedulingSystem.getDoctorById(appointment.getDoctorID());
            System.out.printf("%d. Date: %s, Time: %s, Patient: %s, Doctor: Dr. %s, Appointment ID: %s%n",
                i + 1,
                appointment.getDateTime().toLocalDate(),
                appointment.getDateTime().toLocalTime(),
                appointment.getPatientID(),
                doctor != null ? doctor.getName() : "Unknown",
                appointment.getAppointmentID()
            );
        }
    
        // Let the user select an appointment
        System.out.print("Enter the number of the appointment to " + action + ": ");
        int appointmentIndex = getIntInput() - 1;
    
        if (appointmentIndex >= 0 && appointmentIndex < confirmedAppointments.size()) {
            return confirmedAppointments.get(appointmentIndex);
        } else {
            System.out.println("Invalid selection. No appointment was " + action + "ed.");
            return null;
        }
    }

    /**
     * Views all scheduled appointments for the patient.
     *
     * @param schedulingSystem The scheduling system used to manage appointments.
     */
    public void viewScheduledAppointments(SchedulingSystem schedulingSystem){
        schedulingSystem.viewScheduledAppointmentsForPatient(this);
        }
    
    /**
     * Views past completed appointment outcome records for the patient.
     */
    public void viewPastAppointmentOutcomeRecords() {
    System.out.println("\nPast Completed Appointments for " + getName() + " (Patient ID: " + getuserID() + "):");

    // Filter completed appointments
    List<Appointment> completedAppointments = new ArrayList<>();
    for (Appointment appointment : getAppointments()) {
        if (appointment.getStatus().equals("Completed")) {
            completedAppointments.add(appointment);
        }
    }

    // Handle no completed appointments
    if (completedAppointments.isEmpty()) {
        System.out.println("No past completed appointments found.");
        return;
    }

    // Display completed appointments
    System.out.printf("%-15s %-20s %-20s %-20s %-30s %-50s%n", 
    "Appointment ID", "Date", "Time", "Doctor ID", "Consultation Notes", "Prescribed Medications");
    System.out.println("-------------------------------------------------------------------------------------------------------------");

    for (Appointment appointment : completedAppointments) {
    String prescribedMedications = appointment.getPrescribedMedication() != null && !appointment.getPrescribedMedication().isEmpty()
    ? getPrescribedMedicationDetails(appointment.getPrescribedMedication())
    : "None";

    System.out.printf("%-15s %-20s %-20s %-20s %-30s %-50s%n", 
        appointment.getAppointmentID(), 
        appointment.getDateTime().toLocalDate(), 
        appointment.getDateTime().toLocalTime(), 
        appointment.getDoctorID(), 
        (appointment.getConsultationNotes() != null ? appointment.getConsultationNotes() : "No Notes"),
        prescribedMedications);
    }

    System.out.println("-------------------------------------------------------------------------------------------------------------");
}

    /**
     * Formats the details of prescribed medications for display.
     *
     * @param prescribedMedication A map of medications and their prescribed quantities.
     * @return A formatted string of medication details.
     */
    private static String getPrescribedMedicationDetails(Map<Medication, Integer> prescribedMedication) {
        StringBuilder medicationDetails = new StringBuilder();
        for (Map.Entry<Medication, Integer> entry : prescribedMedication.entrySet()) {
            medicationDetails.append(entry.getKey().getName()) // Assuming Medication has a getName() method
                            .append(" (Quantity: ")
                            .append(entry.getValue())
                            .append("), ");
        }
        // Remove the trailing comma and space, if any
        if (medicationDetails.length() > 0) {
            medicationDetails.setLength(medicationDetails.length() - 2);
        }
        return medicationDetails.toString();
    }

    /**
     * Displays the patient menu options.
     */
    @Override
    public void displayMenu() {
        System.out.println("""
                
                Patient Display Menu: 
                1. View Patient Information
                2. Update Personal Information 
                3. Schedule an Appointment
                4. Reschedule an Appointment
                5. Cancel an Appointment
                6. View Scheduled Appointments
                7. View Past Appointment Outcome Records 
                8. Logout
                Choose options (1-8): """);
    }

    /**
     * Retrieves an integer input from the user.
     *
     * @return The entered integer value.
     */
    private int getIntInput() {
        while (!sc.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            sc.next(); // Clear invalid input
        }
        int input = sc.nextInt();
        sc.nextLine(); // Clear buffer
        return input;
    }

    //private String getStringInput() {
        //return sc.nextLine().trim();
    //}
}