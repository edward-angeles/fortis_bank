package bus;

import dao.AlertDAO;
import dto.AlertDTO;
import java.util.ArrayList;

public class AlertService {

    private AlertDAO dao = new AlertDAO();

    // CREATE
    public AlertDTO create(AlertDTO alert) throws NotNullException {
        validate(alert);
        AlertDTO a = dao.add(alert);
        if (a == null)
            throw new NotNullException("Failed to create alert. Please try again.");
        return a;
    }

    // READ - by ID
    public AlertDTO getById(String id) throws NotNullException {
        validateId(id);
        AlertDTO alert = dao.getById(id);
        if (alert == null)
            throw new NotNullException("No alert found with id: " + id);
        return alert;
    }

    // READ - all
    public ArrayList<AlertDTO> getAll() {
        return dao.getAll();
    }

    // UPDATE
    public int update(AlertDTO alert) throws NotNullException {
        validateId(alert.getId());
        validate(alert);
        // Verify alert exists before updating
        AlertDTO existing = dao.getById(alert.getId());
        if (existing == null)
            throw new NotNullException("No alert found with id: " + alert.getId());
        int result = dao.update(alert);
        if (result == -1)
            throw new NotNullException("Failed to update alert. Please try again.");
        return result;
    }

    // SEND SYSTEM ALERT � creates an alert not linked to any BMC (type 1: success, 2: low balance, 3: unusual activity)
    public AlertDTO sendSystemAlert(String message, int alertTypeId) throws NotNullException {
        if (message == null || message.trim().isEmpty())
            throw new NotNullException("Alert message cannot be null or empty.");
        if (alertTypeId <= 0)
            throw new NotNullException("Alert type ID must be a positive number.");
        if (alertTypeId == 4)
            throw new NotNullException("System alerts cannot be of type 'request account opening'. Use sendAccountRequest() instead.");

        // bmc_id is null for system-generated alerts (not linked to a manager-customer pair)
        AlertDTO alert = new AlertDTO(null, message, null, null);
        alert.setAlertType(new dto.AlertType(alertTypeId, null, null));
        return create(alert);
    }

    // SEND ACCOUNT REQUEST ALERT � type 4, always requires a BMC id
    public AlertDTO sendAccountRequest(String message, String bmcId) throws NotNullException {
        if (message == null || message.trim().isEmpty())
            throw new NotNullException("Alert message cannot be null or empty.");
        validateId(bmcId);
        AlertDTO alert = new AlertDTO(null, message, new dto.AlertType(4, "request account opening", null), bmcId);
        return create(alert);
    }

    private void validate(AlertDTO alert) throws NotNullException {
        if (alert == null)
            throw new NotNullException("Alert cannot be null.");
        if (alert.getMessage() == null || alert.getMessage().trim().isEmpty())
            throw new NotNullException("Alert message cannot be null or empty.");
        if (alert.getAlertType() == null)
            throw new NotNullException("Alert type cannot be null.");
        if (alert.getAlertType().getId() <= 0)
            throw new NotNullException("Alert type ID must be a positive number.");

        // type 4 (request account opening) MUST have a BMC id
        if (alert.getAlertType().getId() == 4) {
            if (alert.getBmc_id() == null || alert.getBmc_id().trim().isEmpty())
                throw new NotNullException("Account opening requests must be linked to a bank manager-customer relationship.");
        }
    }

    private void validateId(String id) throws NotNullException {
        if (id == null || id.trim().isEmpty())
            throw new NotNullException("ID cannot be null or empty.");
        try {
            if (Integer.parseInt(id) <= 0)
                throw new NotNullException("ID must be a positive number.");
        } catch (NumberFormatException e) {
            throw new NotNullException("ID must be a valid number.");
        }
    }
}