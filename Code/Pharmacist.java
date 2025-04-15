import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Represents a pharmacist in the hospital management system.
 * A pharmacist can view appointment outcome records, manage prescriptions, 
 * view and manage inventory, and submit replenishment requests.
 * This class extends the `User` class.
 */
public class Pharmacist extends User {
    private static Scanner sc = new Scanner(System.in);

    /**
     * Constructs a new `Pharmacist` object.
     *
     * @param userID The unique ID of the pharmacist.
     * @param name   The name of the pharmacist.
     * @param gender The gender of the pharmacist.
     */
    public Pharmacist(String userID, String name, String gender) {
        super(userID, null, name, gender, "Pharmacist"); // Default password is null
    }

    /**
 * Retrieves the unique ID of the pharmacist.
 *
 * @return The pharmacist's user ID.
 */
    public String getUserID() {
        return userID;
    }

    /**
 * Displays a list of completed appointments, including details about the consultation notes,
 * prescribed medications, and their statuses.
 *
 * @param patients The list of patients whose appointments are to be checked.
 * @return `true` if completed appointments are found; otherwise, `false`.
 */
    public boolean viewAppointmentOutcomeRecord(List<Patient> patients) {
        System.out.println("\nCompleted Appointments:");
    
        boolean hasCompletedAppointments = false;
    
        for (Patient patient : patients) {
            for (Appointment appointment : patient.getAppointments()) {
                if (appointment.getStatus().equals("Completed")) {
                    hasCompletedAppointments = true;
                    System.out.println(
                        "Date: " + appointment.getDateTime().toLocalDate() +
                        "\nTime: " + appointment.getDateTime().toLocalTime() +
                        "\nPatient: " + patient.getName() +
                        "\nAppointment ID: " + appointment.getAppointmentID() +
                        "\nConsultation Notes: " + (appointment.getConsultationNotes() != null ? appointment.getConsultationNotes() : "None") +
                        "\nPrescribed Medication: " + (appointment.getPrescribedMedication() != null && !appointment.getPrescribedMedication().isEmpty()
                        ? getPrescribedMedicationDetails(appointment.getPrescribedMedication())
                        : "None") +
                        "\nMedication Status: " + (appointment.getMedicationStatus() != null ? appointment.getMedicationStatus() : "Not Prescribed")
                    );
                }
            }
        }
    
        if (!hasCompletedAppointments) {
            System.out.println("No completed appointments found.");
        }

        return hasCompletedAppointments;
    }

    /**
 * Retrieves the details of prescribed medications for an appointment.
 *
 * @param prescribedMedication A map containing medications and their quantities.
 * @return A formatted string containing the medication names and quantities.
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
 * Updates the prescription status of a completed appointment.
 * The pharmacist can dispense medications if the status is pending.
 *
 * @param patients  The list of patients whose appointments are to be updated.
 * @param inventory The inventory of medications.
 */
    public void updatePrescriptionStatus(List<Patient> patients, Inventory inventory) {
        boolean hasCompletedAppointments = viewAppointmentOutcomeRecord(patients);

        if (!hasCompletedAppointments) {
            System.out.println("No completed appointments available.");
            return; // Exit early if no completed appointments
        }
    
        // Prompt for appointment ID
        System.out.println("Enter Appointment ID to update status: ");
        String appointmentID = sc.next();
        
        
        Appointment targetAppointment = null;
    
        // Find the target appointment by Appointment ID
        for (Patient patient : patients) {
            for (Appointment appointment : patient.getAppointments()) {
                if (appointment.getAppointmentID().equals(appointmentID)) {
                    targetAppointment = appointment;
                    break;
                }
            }
            if (targetAppointment != null) {
                break;
            }
        }
    
        if (targetAppointment == null) {
            System.out.println("Appointment not found.");
            return;
        }
    
        // Check the medication status
        if ("Dispense Complete".equals(targetAppointment.getMedicationStatus())) 
        {
            System.out.println("Medication already dispensed.");
        } else if ("Pending to Dispense".equals(targetAppointment.getMedicationStatus())) 
        {
            System.out.println("Pending medication prescription found.");
            System.out.println("Do you want to dispense medications? (1. Yes / 2. No)");
            int choice = sc.nextInt();
    
            if (choice == 1) {
                Map<Medication, Integer> medications = targetAppointment.getPrescribedMedication();
    
                if (medications.isEmpty()) {
                    System.out.println("No medications prescribed for this appointment.");
                    return;
                }
    
                for (Map.Entry<Medication, Integer> entry : medications.entrySet()) {
                    Medication medication = entry.getKey();
                    int quantity = entry.getValue();
    
                    if (quantity > medication.getQuantity()) {
                        System.out.println("Insufficient Medication: " + medication.getName() + " in Inventory.");
                        return;
                    } else {
                        System.out.println("Dispensing Medication: " + medication.getName() + " (Quantity: " + quantity + ")");
                        medication.updateQuantity(medication.getQuantity() - quantity);
                    }
                }
    
                // Mark medication status as dispensed
                targetAppointment.completeDispense();
                System.out.println("Medication dispensed successfully. Status updated to 'Dispense Complete'.");
            } else {
                System.out.println("Dispensing canceled.");
            }
        } else {
            System.out.println("No pending medication prescriptions for this appointment.");
        }
    }

    /**
 * Displays the current medication inventory.
 *
 * @param inventory The inventory of medications.
 */
    public void viewInventory(Inventory inventory) {
        System.out.println("Viewing medication inventory:");
        inventory.displayInventory(false);
    }

    /**
 * Submits a replenishment request for a specific medication.
 *
 * @param inventory The inventory of medications.
 */
    public void submitReplenishmentRequest(Inventory inventory) {
        inventory.displayInventory(false);
        System.out.println("Submit Replenishment Request for Medication ID: ");
        String medicationID = sc.next();
        System.out.println("Amount to replenish: ");
        int replenishAmount = sc.nextInt();
        inventory.newReplenishmentRequest(medicationID, replenishAmount);
    }

    /**
 * Updates the role of a pharmacist to either "Doctor" or "Administrator."
 * A new corresponding object is created, and the pharmacist is added to the respective list.
 *
 * @param pharmacist    The `Pharmacist` object whose role is being updated.
 * @param newRole       The new role for the pharmacist (1 for Doctor, 3 for Administrator).
 * @param doctors       The list of doctors in the system, to which the pharmacist may be added.
 * @param administrators The list of administrators in the system, to which the pharmacist may be added.
 */
    public void updateRole(Pharmacist pharmacist, int newRole, List<Doctor> doctors, List<Administrator> administrators) {
        switch (newRole) {
            case 1:
                this.role = "Doctor";
                System.out.println("Enter Doctor Specialty: ");
                sc.nextLine();
                String specialty = sc.nextLine();
                Doctor doctor = new Doctor(pharmacist.getuserID(), pharmacist.getName(), pharmacist.getGender(), specialty);
                doctors.add(doctor);
                break;
            case 3:
                this.role = "Administrator";
                Administrator administrator = new Administrator(pharmacist.getuserID(), pharmacist.getName(), pharmacist.getGender());
                administrators.add(administrator);
                break;
            default:
                System.out.println("Invalid option. Please try again.");
                return;
        }
    }

    /**
 * Searches for a pharmacist by their unique ID in the list of pharmacists.
 *
 * @param pharmacistID The unique ID of the pharmacist to find.
 * @param pharmacists  The list of pharmacists in which to search.
 * @return The `Pharmacist` object with the specified ID, or `null` if not found.
 */
    public static Pharmacist findPharmacistByID(String pharmacistID, List<Pharmacist> pharmacists) {
        for (Pharmacist pharmacist : pharmacists) {
            if (pharmacist.getuserID().equals(pharmacistID)) {
                return pharmacist;
            }
        }
        return null;
    }

    /**
 * Displays the menu options available to the pharmacist.
 * The pharmacist can perform actions such as viewing appointment outcomes,
 * updating prescription statuses, managing the medication inventory, and submitting replenishment requests.
 */
    @Override
    public void displayMenu() {
        System.out.println("""
                
                Pharmacist Display Menu: 
                1. View Appointment Outcome Record
                2. Update Prescription Status
                3. View Medication Inventory
                4. Submit Replenishment Request
                5. Logout
                Choose options (1-5): """);

    }
}
