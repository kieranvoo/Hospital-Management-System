import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


/**
 * The Doctor class represents a doctor in the hospital management system.
 * Doctors can manage their schedules, view and update patient medical records,
 * respond to appointment requests, and set their availability for appointments.
 */
public class Doctor extends User {
    /** The unique identifier for the doctor. */
    private String doctorID;
    /** The specialty of the doctor (e.g., "Cardiology", "Radiology"). */
    private String specialty;
    /** A list of appointments scheduled for the doctor. */
    private List<Appointment> schedule; //schedule of appts
    /** A map of availability slots for each date. */
    private Map<LocalDate, List<TimeRange>> availability; //daily availability
    /** A map of blocked slots for each date, indicating times the doctor is unavailable. */
    private Map<LocalDate, List<TimeRange>> blockedSlots;


    private static Scanner sc = new Scanner(System.in);

    /**
     * Constructor for the Doctor class.
     *
     * @param userID    The unique ID of the doctor.
     * @param name      The name of the doctor.
     * @param gender    The gender of the doctor.
     * @param specialty The specialty of the doctor.
     */
    public Doctor(String userID, String name, String gender, String specialty) {
        super(userID, null, name, gender, "Doctor"); // Default password is null
        this.doctorID =userID;
        this.specialty = specialty;
        this.schedule = new ArrayList<>();
        this.availability = new HashMap<>();
        this.blockedSlots= new HashMap<>();
    }
    
    /**
 * Retrieves the doctor's specialty.
 *
 * @return The specialty of the doctor as a {@code String}.
 */
    public String getSpecialty() {
        return specialty;
    }
    
    /**
     * Updates the specialty of the doctor.
     *
     * @param specialty The new specialty.
     */
    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }
    
    /**
 * Retrieves the unique ID of the doctor.
 *
 * @return The unique doctor ID as a {@code String}.
 */
    public String getDoctorID() {
        return doctorID;
    }

    /**
 * Retrieves the doctor's availability for appointments.
 * The availability is represented as a map of dates to time ranges.
 *
 * @return A map of {@code LocalDate} to {@code List<TimeRange>} representing availability.
 */
    public Map<LocalDate, List<TimeRange>> getAvailability() {
            return availability;
        }

    /**
 * Displays a list of all patients in the hospital system.
 * The list includes patient IDs and names.
 *
 * @param patients The list of all patients to display.
 */
        public void viewAllPatients(List<Patient> patients) {
        System.out.println("=======================================");
        System.out.println("             All Patients              ");
        System.out.println("=======================================");
        
        System.out.printf(
            "%-15s %-20s%n",
            "Patient ID",
            "Name"
        );
    
        System.out.println("---------------------------------------");
        
        for (Patient patient : patients) {
            System.out.printf(
                "%-15s %-20s%n",
                patient.getuserID(),
                patient.getName()
            );
        }
        System.out.println("=======================================");
    }

    /**
 * Views the medical record of a specific patient.
 *
 * @param patient The {@code Patient} whose medical record is to be viewed.
 */
    public void viewPatientMedicalRecord(Patient patient) {
        System.out.println("Viewing medical record for Patient ID: " + patient.getuserID());
        System.out.println(patient.getMedicalRecord().toString());
    }

    /**
 * Updates the medical record of a specific patient by adding a diagnosis or treatment.
 *
 * @param patient The {@code Patient} whose medical record is to be updated.
 */
    public void updatePatientMedicalRecord(Patient patient) {
        System.out.println("Updating medical record for Patient ID: " + patient.getuserID());
        
        try {
            System.out.println("""
                    1. Diagnosis
                    2. Treatment
                    Choose options (1-2) to update: """);
            int option = sc.nextInt();

            switch (option) {
                case 1:
                    System.out.println("Update diagnosis: ");
                    sc.nextLine();
                    String newDiagnosis = sc.nextLine();
                    patient.getMedicalRecord().addDiagnosis(newDiagnosis);
                    System.out.println("Diagnosis successfully added for patient " + patient.getName());
                    break;
                case 2:
                    System.out.println("Update treatment: ");
                    sc.nextLine();
                    String newTreatment = sc.nextLine();
                    patient.getMedicalRecord().addTreatment(newTreatment);
                    System.out.println("Treatment successfully added for patient " + patient.getName());
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            sc.nextLine(); // Clear the invalid input from the scanner buffer
        }
    }

    /**
 * Sets the doctor's availability for appointments.
 * The doctor can either use default availability or customize blocked times.
 */
    public void setAvailability() {
        System.out.println("""
            Welcome to setting availability for appointments.
            Standard working hours (09:00–12:00, 13:00–18:00) will be generated automatically,
            excluding work engagements and blocked times.
            
            Select an option:
            1. Customize availability (block additional times)
            2. Use default availability """);

        int choice = getIntInput();
        if (choice == 1) {
            handleCustomBlockedTimes();
        }

        generateStandardAvailability();
        System.out.println("Availability has been updated.");

        SchedulingSystem schedulingSystem = Main.getSchedulingSystem();
        schedulingSystem.updateDoctorAvailability(this);
    }

    /**
 * Checks for conflicts with the doctor's schedule, including appointments and blocked slots.
 *
 * @param start The start time of the slot to check as a {@code LocalDateTime}.
 * @param end   The end time of the slot to check as a {@code LocalDateTime}.
 * @return {@code true} if a conflict exists; {@code false} otherwise.
 */
    public boolean hasConflict(LocalDateTime start, LocalDateTime end) {
        // Check for conflicts with scheduled appointments
        for (Appointment appointment : schedule) {
            LocalDateTime apptStart = appointment.getDateTime();
            LocalDateTime apptEnd = apptStart.plusMinutes(30); // Assuming 30-minute slots
            if (!(end.isBefore(apptStart) || start.isAfter(apptEnd))) {
                return true; // Conflict with an appointment
            }
        }

        // Check for conflicts with blocked slots
        List<TimeRange> blocked = blockedSlots.getOrDefault(start.toLocalDate(), new ArrayList<>());
        for (TimeRange block : blocked) {
            if (!(end.toLocalTime().isBefore(block.getStart()) || start.toLocalTime().isAfter(block.getEnd()))) {
                return true; // Conflict with a blocked slot
            }
        }

        return false; // No conflict
    }

    /**
     * Handles custom blocked times for the doctor's schedule.
     */
    private void handleCustomBlockedTimes() {
        System.out.println("Enter the number of custom blocks:");
        int numBlocks = getIntInput();

        for (int i = 0; i < numBlocks; i++) {
            System.out.println("Enter the date to block (yyyy-MM-dd):");
            LocalDate blockDate = LocalDate.parse(sc.nextLine());

            System.out.println("Enter the start time to block (HH:mm):");
            LocalTime blockStart = LocalTime.parse(sc.nextLine());

            System.out.println("Enter the end time to block (HH:mm):");
            LocalTime blockEnd = LocalTime.parse(sc.nextLine());

            System.out.println("Enter the event name (if any):");
            String eventName = sc.nextLine();

            LocalDateTime blockStartDateTime = blockDate.atTime(blockStart);
            LocalDateTime blockEndDateTime = blockDate.atTime(blockEnd);

            // Use the conflict checking method
            if (hasConflict(blockStartDateTime, blockEndDateTime)) {
                System.out.println("Conflict detected. Unable to block the time slot.");
                continue; // Skip this block
            }

            // Add block to the blockedSlots map
            blockedSlots.computeIfAbsent(blockDate, k -> new ArrayList<>())
                .add(new TimeRange(blockStart, blockEnd, eventName));
            System.out.println("Blocked: " + blockDate + " from " + blockStart + " to " + blockEnd +
                (eventName.isEmpty() ? "" : " (" + eventName + ")"));
        }
    }

    /**
     * Generates standard availability slots for the doctor.
     */
    private void generateStandardAvailability() {
        LocalDate startDate = LocalDate.now();
        int days = 7;
        int slotDuration = 30;

        for (int i = 0; i < days; i++) {
            LocalDate currentDate = startDate.plusDays(i);
            List<TimeRange> dailySlots = new ArrayList<>();
            generateTimeSlots(dailySlots, LocalTime.of(9, 0), LocalTime.of(12, 0), slotDuration);
            generateTimeSlots(dailySlots, LocalTime.of(13, 0), LocalTime.of(18, 0), slotDuration);

            List<TimeRange> customBlocks = blockedSlots.getOrDefault(currentDate, new ArrayList<>());
            for (TimeRange block : customBlocks) {
                dailySlots.removeIf(slot -> slot.overlaps(block));
            }

            availability.put(currentDate, dailySlots);
        }
    }

    /**
     * Generates time slots within a specified range.
     *
     * @param slots               The list to populate with generated time slots.
     * @param startTime           The start time of the range.
     * @param endTime             The end time of the range.
     * @param slotDurationMinutes The duration of each time slot in minutes.
     */
    private void generateTimeSlots(List<TimeRange> slots, LocalTime startTime, LocalTime endTime, int slotDurationMinutes) {
        LocalTime currentTime = startTime;
        while (currentTime.plusMinutes(slotDurationMinutes).isBefore(endTime) || currentTime.plusMinutes(slotDurationMinutes).equals(endTime)) {
            slots.add(new TimeRange(currentTime, currentTime.plusMinutes(slotDurationMinutes)));
            currentTime = currentTime.plusMinutes(slotDurationMinutes);
        }
    }

    /**
     * Views the doctor's personal schedule, including appointments and blocked times.
     */
    public void viewPersonalSchedule() {
        System.out.println("\nPersonal Schedule for Dr. " + name + ", Doctor ID: " + doctorID);

        List<ScheduledEvent> events = new ArrayList<>();


        // Add upcoming appointments to events
        for (Appointment appointment : schedule) {
            if (!appointment.getStatus().equals("Completed") && !appointment.getStatus().equals("Cancelled")) {
                LocalDateTime start = appointment.getDateTime();
                LocalDateTime end = start.plusMinutes(30); // Assume appointments are 30 minutes long
                events.add(new ScheduledEvent(start, end, "Appointment with Patient: " + appointment.getPatientID() +
                    ", Appointment ID: " + appointment.getAppointmentID()));
                }
            }


        // Add blocked slots to events
        for (Map.Entry<LocalDate, List<TimeRange>> entry : blockedSlots.entrySet()) {
            LocalDate date = entry.getKey();
            for (TimeRange range : entry.getValue()) {
                LocalDateTime start = date.atTime(range.getStart());
                LocalDateTime end = date.atTime(range.getEnd());
                events.add(new ScheduledEvent(start, end, "Blocked Time: " + range.getStart() + " to " + range.getEnd() +
                        (range.getEventName() != null && !range.getEventName().isEmpty() ? ", Event: " + range.getEventName() : "")));
            }
        }

        // Sort events by start time
        events.sort(Comparator.comparing(ScheduledEvent::getStart));

        // Print events
        if (events.isEmpty()) {
            System.out.println("No events scheduled.");
        } else {
            System.out.println("--- Schedule ---");
            for (ScheduledEvent event : events) {
                System.out.println("Date: " + event.getStart().toLocalDate() +
                        ", Time: " + event.getStart().toLocalTime() + " to " + event.getEnd().toLocalTime() +
                        " - " + event.getDescription());
            }
            System.out.println("----------------");
        }
    }

    /**
 * A helper class to represent scheduled events in a doctor's schedule.
 * This class is used for sorting and displaying events such as appointments and blocked time slots.
 */
    private static class ScheduledEvent {
        private LocalDateTime start;
        private LocalDateTime end;
        private String description;

        public ScheduledEvent(LocalDateTime start, LocalDateTime end, String description) {
            this.start = start;
            this.end = end;
            this.description = description;
        }

        /**
        * @return The start time of the event.
        */
        public LocalDateTime getStart() {
            return start;
        }
        
        /**
     * @return The end time of the event.
     */
        public LocalDateTime getEnd() {
            return end;
        }

        /**
     * @return The description of the event.
     */
        public String getDescription() {
            return description;
        }
    }

    /**
 * Helper method to validate and retrieve integer input from the user.
 * Ensures the user enters a valid integer and handles invalid input gracefully.
 *
 * @return A valid integer input from the user.
 */
    private int getIntInput() {
        while (!sc.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            sc.next();
        }
        int input = sc.nextInt();
        sc.nextLine();
        return input;
    }

    /** A list of pending appointments for the doctor. */
    private List<Appointment> pendingAppointments = new ArrayList<>();

    /**
     * Adds a pending appointment to the doctor's pending appointments list.
     *
     * @param appointment The appointment to add.
     */
    public void addPendingAppointment(Appointment appointment) {
        pendingAppointments.add(appointment);
    }

    /**
 * Handles responses to pending appointment requests.
 * The doctor can accept or reject the requests.
 *
 * @param schedulingSystem The {@code SchedulingSystem} instance to manage appointments.
 */
    public void respondToAppointmentRequests(SchedulingSystem schedulingSystem) {
        // Display pending appointments for the doctor
        schedulingSystem.viewPendingAppointmentsForDoctor(this);
    
        // Check if there are any pending appointments
        boolean hasPendingRequests = schedulingSystem.getPendingAppointmentsForDoctor(this).isEmpty();
        if (hasPendingRequests) {
            //System.out.println("No pending appointment requests found.");
            return; // Exit if no pending requests
        }
    
        // Ask for response only if there are pending appointments
        System.out.println("Enter the Appointment ID to respond to: ");
        String appointmentID = sc.next();
    
        Appointment appointment = schedulingSystem.getPendingAppointmentById(appointmentID);
        if (appointment == null) {
            System.out.println("Invalid Appointment ID.");
            return;
        }
    
        System.out.println("1. Accept\n2. Reject");
        int choice = sc.nextInt();
        //sc.nextLine(); // Clear buffer
    
        if (choice == 1) {
            schedulingSystem.respondToPendingAppointment(appointment, true);
        } else if (choice == 2) {
            schedulingSystem.respondToPendingAppointment(appointment, false);
        } else {
            System.out.println("Invalid choice.");
        }
    }
    

    /**
 * Adds an appointment to the doctor's schedule and removes the corresponding time slot
 * from the doctor's availability.
 *
 * @param appointment The appointment to be added.
 */
    public void addAppointment(Appointment appointment) {
        schedule.add(appointment);

        // Remove the time slot from availability
        LocalDate date = appointment.getDateTime().toLocalDate();
        LocalTime time = appointment.getDateTime().toLocalTime();

        List<TimeRange> ranges = availability.get(date);
        if (ranges != null) {
            ranges.removeIf(range -> !time.isBefore(range.getStart()) && !time.isAfter(range.getEnd()));
        }
    }

    /**
 * Cancels an appointment and restores the corresponding time slot to the doctor's availability.
 *
 * @param appointment The {@code Appointment} to be canceled.
 */
    public void cancelAppointment(Appointment appointment) {
        if (schedule.remove(appointment)) {
            LocalDate date = appointment.getDateTime().toLocalDate();
            LocalTime time = appointment.getDateTime().toLocalTime();

            // Restore the time slot to availability
            availability.computeIfAbsent(date, k -> new ArrayList<>())
                .add(new TimeRange(time, time.plusMinutes(30))); // Adjust time range if needed

            System.out.println("Appointment removed from schedule and availability updated.");
        } else {
            System.out.println("Appointment not found in schedule.");
        }
    }

    /**
    * Displays a list of all upcoming confirmed appointments for the doctor,
    * sorted by date and time.
    */
    public void viewUpcomingAppointments() {
    System.out.println("\nUpcoming Appointments for Dr. " + name + ", Doctor ID: " + doctorID);

    LocalDateTime now = LocalDateTime.now(); // Current time
    List<Appointment> upcomingAppointments = new ArrayList<>();

    // Filter for future confirmed appointments for this doctor
    for (Appointment appointment : schedule) {
        if (appointment.getDateTime().isAfter(now) && appointment.getStatus().equals("Confirmed")) {
            upcomingAppointments.add(appointment);
        }
    }

    // Sort appointments by date and time
    upcomingAppointments.sort(Comparator.comparing(Appointment::getDateTime));

    if (upcomingAppointments.isEmpty()) {
        System.out.println("No upcoming appointments.");
    } else {
        System.out.println("--- Appointments ---");
        for (Appointment appointment : upcomingAppointments) {
            System.out.println(
                "Date: " + appointment.getDateTime().toLocalDate() + 
                ", Time: " + appointment.getDateTime().toLocalTime() +
                ", Patient: " + appointment.getPatientID() +
                ", Appointment ID: " + appointment.getAppointmentID()
            );
        }
    }

    System.out.println("--------------------");
}


    
/**
 * Records the outcome of a confirmed appointment.
 * Prompts the doctor to add prescribed medications and consultation notes.
 *
 * @param inventory The {@code Inventory} instance to manage prescribed medications.
 */
public void recordAppointmentOutcome(Inventory inventory) {
    System.out.println("\nConfirmed Appointments for Dr. " + getName() + ":");

    // Filter and display confirmed appointments
    List<Appointment> confirmedAppointments = new ArrayList<>();
    for (Appointment appointment : schedule) {
        if (appointment.getStatus().equals("Confirmed")) {
            confirmedAppointments.add(appointment);
            System.out.printf("Appointment ID: %s | Date & Time: %s | Patient ID: %s%n",
                appointment.getAppointmentID(),
                appointment.getDateTime().toLocalDate() + " " + appointment.getDateTime().toLocalTime(),
                appointment.getPatientID());
        }
    }

    // Handle no confirmed appointments
    if (confirmedAppointments.isEmpty()) {
        System.out.println("No confirmed appointments to record outcome for.");
        return;
    }

    // Prompt doctor to choose an appointment
    System.out.println("\nEnter the Appointment ID to record the outcome:");
    String appointmentID = sc.next();

    Appointment selectedAppointment = null;
    for (Appointment appointment : confirmedAppointments) {
        if (appointment.getAppointmentID().equals(appointmentID)) {
            selectedAppointment = appointment;
            break;
        }
    }

    if (selectedAppointment == null) {
        System.out.println("Invalid Appointment ID. Please try again.");
        return;
    }

    // Complete the appointment
    selectedAppointment.complete(inventory, appointmentID);
}


    /**
     * Updates the doctor's role, allowing the administrator to reassign them to other roles.
     *
     * @param doctor         The doctor instance to update.
     * @param newRole        The new role to assign (e.g., Pharmacist or Administrator).
     * @param pharmacists    The list of pharmacists to add the doctor to, if applicable.
     * @param administrators The list of administrators to add the doctor to, if applicable.
     */
    public void updateRole(Doctor doctor, int newRole, List<Pharmacist> pharmacists, List<Administrator> administrators) {
        switch (newRole) {
            case 2:
                this.role = "Pharmacist";
                Pharmacist pharmacist = new Pharmacist(doctor.getuserID(), doctor.getName(), doctor.getGender());
                pharmacists.add(pharmacist);
                break;
            case 3:
                this.role = "Administrator";
                Administrator administrator = new Administrator(doctor.getuserID(), doctor.getName(), doctor.getGender());
                administrators.add(administrator);
                break;
            default:
                System.out.println("Invalid option. Please try again.");
                return;
        }
    }
    
    /**
 * Finds a doctor by their unique ID from a list of doctors.
 *
 * @param doctorID The ID of the doctor to find.
 * @param doctors  The list of all doctors.
 * @return The `Doctor` object if found, otherwise `null`.
 */
    public static Doctor findDoctorByID(String doctorID, List<Doctor> doctors) {
        for (Doctor doctor : doctors) {
            if (doctor.getuserID().equals(doctorID)) {
                return doctor;
            }
        }
        return null;
    }

     /**
 * Displays the doctor's menu for managing schedules, appointments, and patient records.
 */
    @Override
    public void displayMenu() {
        System.out.println("""
                
                Doctor Display Menu: 
                1. View Patient Medical Records 
                2. Update Patient Medical Records
                3. View Personal Schedule
                4. Set Availability for Appointments
                5. Respond to Appointment Requests
                6. View Upcoming Appointments
                7. Record Appointment Outcome 
                8. Logout
                Choose options (1-8): """);
    }
}
