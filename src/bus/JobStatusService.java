
package bus;

import dao.JobStatusDAO;
import dto.JobStatus;
import java.sql.SQLException;
import java.util.List;

public class JobStatusService {

    private JobStatusDAO dao = new JobStatusDAO();

    // CREATE
    public void create(JobStatus status) throws SQLException, NotNullException {
        validate(status);
        dao.insert(status);
    }

    // READ - by ID
    public JobStatus getById(int id) throws SQLException, NotNullException {
        if (id <= 0)
            throw new NotNullException("Job status ID must be a positive number.");
        JobStatus status = dao.getById(id);
        if (status == null)
            throw new NotNullException("No job status found with id: " + id);
        return status;
    }

    // READ - all
    public List<JobStatus> getAll() throws SQLException {
        return dao.getAll();
    }

    // UPDATE
    public void update(JobStatus status) throws SQLException, NotNullException {
        if (status.getId() <= 0)
            throw new NotNullException("Job status ID must be a positive number.");
        validate(status);
        dao.update(status);
    }

    // DELETE
    public void delete(int id) throws SQLException, NotNullException {
        if (id <= 0)
            throw new NotNullException("Job status ID must be a positive number.");
        dao.delete(id);
    }

    private void validate(JobStatus status) throws NotNullException {
        if (status == null)
            throw new NotNullException("Job status cannot be null.");
        if (status.getName() == null || status.getName().trim().isEmpty())
            throw new NotNullException("Job status name cannot be null or empty.");
        if (status.getName().length() > 20)
            throw new NotNullException("Job status name cannot exceed 20 characters.");
    }
}

