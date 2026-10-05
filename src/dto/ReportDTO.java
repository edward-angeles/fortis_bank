package dto;


import java.time.LocalDateTime;

public class ReportDTO
{
    // Attributes
    private String reportId;
	private LocalDateTime generatedAt;
    private ReportType type;
    private String reportContent;
    private String managerId;
    private String customerId;

    // Getters & Setters
    public String getReportId()
    {
        return reportId;
    }

    public void setReportId(String reportId)
    {
        this.reportId = reportId;
    }

    public LocalDateTime getGeneratedAt()
    {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt)
    {
        this.generatedAt = generatedAt;
    }

    public ReportType getType()
    {
        return type;
    }

    public void setType(ReportType type)
    {
        this.type = type;
    }

    public String getReportContent()
    {
        return reportContent;
    }

    public void setReportContent(String reportContent)
    {
        this.reportContent = reportContent;
    }

    public String getManagerId()
    {
        return managerId;
    }

    public void setManagerId(String managerId)
    {
        this.managerId = managerId;
    }

    public String getCustomerId()
    {
        return customerId;
    }

    public void setCustomerId(String customerId)
    {
        this.customerId = customerId;
    }

    // Constructor with parameters
    public ReportDTO(String reportId, LocalDateTime generatedAt, ReportType type, String reportContent, String managerId, String customerId)
    {
        this.reportId = reportId;
        this.generatedAt = generatedAt;
        this.type = type;
        this.reportContent = reportContent;
        this.managerId = managerId;
        this.customerId = customerId;
    }

    @Override
	public String toString() {
		return "ReportDTO [reportId=" + reportId + ", generatedAt=" + generatedAt + ", type=" + type
				+ ", reportContent=" + reportContent + ", managerId=" + managerId + ", customerId=" + customerId + "]";
	}

    // Method
//    public int sendByEmail()
//    {
//        if (this.getReportContent() == null || this.getReportContent().isEmpty())
//        {
//            System.out.println("Error: report content is empty.");
//            return -1;
//        }
//
//        System.out.println("Report sent by email.");
//        return 1;
//    }

}