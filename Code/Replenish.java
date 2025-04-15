/**
 * Interface defining methods related to inventory replenishment in the hospital management system.
 * Provides functionalities for updating low stock alerts, viewing replenishment requests,
 * submitting new requests, and fulfilling existing requests.
 */
public interface Replenish{
    /**
     * Updates the low stock alert level for a specific medication in the inventory.
     */
    void updateLowStockAlert();
    
    /**
     * Displays the list of replenishment requests for medications in the inventory.
     *
     * @return `true` if there are replenishment requests to view; `false` otherwise.
     */
    boolean viewReplenishmentRequests();

    /**
     * Submits a new replenishment request for a specific medication in the inventory.
     *
     * @param medicationID     The unique ID of the medication to replenish.
     * @param replenishAmount  The amount to replenish.
     */
    void newReplenishmentRequest(String medicationID, int replenishAmount);

    /**
     * Fulfills an existing replenishment request for a specific medication in the inventory.
     *
     * @param medicationID The unique ID of the medication whose replenishment request is to be fulfilled.
     */
    void fulfillReplenishmentRequest(String medicationID);
}