import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Represents the inventory of medications in the hospital management system.
 * The `Inventory` class provides methods for adding, removing, updating,
 * and managing medication stock, as well as handling replenishment requests.
 */
public class Inventory implements InventoryManagement, Replenish {
    private List<Medication> medications;
    private static Scanner sc = new Scanner(System.in);

    public Inventory() {
        this.medications = new ArrayList<>();
    }

    /**
    * Adds a new medication to the inventory. Ensures the medication ID and name are unique.
    */
    public void addMedication() {
        System.out.println("Enter Medication ID: ");
        String medicationID = sc.next();
        System.out.println("Enter Medication Name: ");
        sc.nextLine();
        String name = sc.nextLine();
        System.out.println("Enter Medication Quantity: ");
        int quantity = sc.nextInt();

        if (findMedicationByID(medicationID) != null) {
            System.out.println("Medication ID already exists. Please try again.");
            return;
        }

        for (Medication medication : medications) {
            if (medication.getName().trim() == name.trim()) {
                System.out.println("Medication Name already exists. Please try again.");
                return;
            }
        }

        Medication newMedication = new Medication(medicationID, name, quantity, 5); // lowStockAlert as 5 as the default
        medications.add(newMedication);
        System.out.println("Medication " + newMedication.getName() + " successfully added to inventory.");
    }
    
    /**
    * Removes a medication from the inventory by its ID.
    * Displays an error message if the medication is not found.
    */
    public void removeMedication() {
        System.out.println("Enter Medication ID: ");
        String medicationID = sc.next();

        Medication medication = findMedicationByID(medicationID);
        
        if (medication != null) {
            medications.remove(medication);
            System.out.println("Medication " + medication.getName() + " successfully removed from inventory.");
        } else {
            System.out.println("Medication does not exist in inventory. Please try again.");
            return;
        }
    }

    /**
    * Updates the stock quantity of a specific medication in the inventory.
    * Displays an error message if the medication is not found.
    */
    public void updateStock() {
        System.out.println("Enter Medication ID: ");
        String medicationID = sc.next();
        Medication medication = findMedicationByID(medicationID);

        if (medication != null) {
            System.out.println("Current Quantity for Medication " + medication.getName() + ": " + medication.getQuantity());

            System.out.println("New Quantity: ");
            int newQuantity = sc.nextInt();

            medication.updateQuantity(newQuantity);
            System.out.println("Medication " + medication.getName() + " stock successfully updated to " + medication.getQuantity() + ".");
        } else {
            System.out.println("Medication does not exist in inventory. Please try again.");
            return;
        }        
    }

    /**
 * Updates the low stock alert level for a specific medication in the inventory.
 * Displays an error message if the medication is not found.
 */
    public void updateLowStockAlert() {
        System.out.println("Enter Medication ID: ");
        String medicationID = sc.next();
        Medication medication = findMedicationByID(medicationID);

        if (medication != null) {
            System.out.println("Current Low Stock Alert Level for Medication " + medication.getName() + ": " + medication.getLowStockAlert());
        
            System.out.println("New Low Stock Alert Level: ");
            int newQuantity = sc.nextInt();

            medication.updateLowStockAlert(newQuantity);
            System.out.println("Medication " + medication.getName() + " Low Stock Alert successfully updated to " + medication.getLowStockAlert() + ".");
        } else {
            System.out.println("Medication does not exist in inventory. Please try again.");
            return;
        }
    }

    /**
 * Displays the inventory of medications. Optionally, filters by medications with replenishment requests.
 *
 * @param replenishmentRequest If true, only displays medications with pending replenishment requests.
 * @return True if the inventory is empty or there are no replenishment requests, false otherwise.
 */
    public boolean displayInventory(boolean replenishmentRequest) {
        boolean empty = true;

        System.out.println("===============================================================================");
        if (replenishmentRequest) {
            System.out.println("                          Replenishment Requests                               ");
        } else {
            System.out.println("                             Current Inventory                                 ");
        }
        System.out.println("===============================================================================");
        
        System.out.printf(
            "%-15s %-20s %-10s %-15s %-15s%n",
            "Medication ID",
            "Name",
            "Quantity",
            "Replenishment",
            "Low Stock Alert"
        );
    
        System.out.println("-------------------------------------------------------------------------------");
        
        for (Medication medication : medications) {
            if (replenishmentRequest) {
                if (medication.getReplenishmentRequest() == 0) {
                    continue;
                }
            }
            
            if (empty == true) {
                empty = false;
            }

            System.out.printf(
                "%-15s %-20s %-10d %-15d %-15s%n",
                medication.getMedicationID(),
                medication.getName(),
                medication.getQuantity(),
                medication.getReplenishmentRequest(),
                medication.getLowStockAlert()
            );
        }
        System.out.println("===============================================================================");

        return empty;
    }

    /**
 * Adds a medication to the inventory. If the medication already exists, it should be handled elsewhere.
 *
 * @param medication The medication to add to the inventory.
 */
    public void updateInventory(Medication medication) {
        this.medications.add(medication);
    }

    /**
 * Displays all medications in the inventory that have a pending replenishment request.
 *
 * @return True if no replenishment requests exist, false otherwise.
 */
    public boolean viewReplenishmentRequests() {
        return displayInventory(true);
    }

    /**
 * Submits a new replenishment request for a specific medication.
 * Increases the replenishment request amount for the medication if it exists.
 *
 * @param medicationID    The ID of the medication to request replenishment for.
 * @param replenishAmount The amount to replenish.
 */
    public void newReplenishmentRequest(String medicationID, int replenishAmount) {
        Medication medication = findMedicationByID(medicationID);

        if (medication != null) {
            medication.updateReplenishmentRequest(medication.getReplenishmentRequest() + replenishAmount);
            System.out.println("Replenishment Request Submitted.");
        } else {
            System.out.println("Medication not found.");
            return;
        }
    }

    /**
 * Fulfills a replenishment request for a specific medication.
 * Adds the replenishment amount to the medication's stock and resets the request to zero.
 *
 * @param medicationID The ID of the medication to fulfill the replenishment request for.
 */
    public void fulfillReplenishmentRequest(String medicationID) {
        Medication medication = findMedicationByID(medicationID);

        if (medication != null) {
            if (medication.getReplenishmentRequest() != 0) {
                medication.updateQuantity(medication.getQuantity() + medication.getReplenishmentRequest());
                medication.updateReplenishmentRequest(0);
                System.out.println("Replenishment for Medication " + medication.getName() + " successful.");
            } else {
                System.out.println("Error: No replenishment request.");
            }
        }
    }

    /**
 * Finds a medication in the inventory by its ID.
 *
 * @param medicationID The ID of the medication to find.
 * @return The `Medication` object if found, otherwise `null`.
 */
    public Medication findMedicationByID(String medicationID) {
        for (Medication medication : medications) {
            if (medication.getMedicationID().equals(medicationID)) {
                return medication;
            }
        }
        return null;
    }
}