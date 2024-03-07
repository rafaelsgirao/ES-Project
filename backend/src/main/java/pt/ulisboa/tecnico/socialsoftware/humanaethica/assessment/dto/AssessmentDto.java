package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.UserDto;

public class AssessmentDto {

  private Integer id;
  private String review;
  private String reviewDate;
  private UserDto volunteer;
  private InstitutionDto institution;

  public AssessmentDto() {
  }

  public AssessmentDto(Assessment assessment, boolean deepCopyInstitution, boolean deepCopyVolunteer) {
    setId(assessment.getId());
    setReview(assessment.getReview());
    setReviewDate(DateHandler.toISOString(assessment.getReviewDate()));

    if(deepCopyVolunteer && assessment.getVolunteer() != null){
      setVolunteer(new UserDto(assessment.getVolunteer()));
    }

    if(deepCopyInstitution && assessment.getInstitution() != null){
      setInstitution(new InstitutionDto(assessment.getInstitution()));
    }
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getReview() {
    return review;
  }

  public void setReview(String review) {
    this.review = review;
  }

  public String getReviewDate() {
    return reviewDate;
  }

  public void setReviewDate(String reviewDate) {
    this.reviewDate = reviewDate;
  }

  public UserDto getVolunteer() {
    return volunteer;
  }

  public void setVolunteer(UserDto volunteer) {
    this.volunteer = volunteer;
  }

  public InstitutionDto getInstitution() {
    return institution;
  }

  public void setInstitution(InstitutionDto institution) {
    this.institution = institution;
  }

  public String toString() {
    return "AssessmentDto{" +
      "id=" + id +
      ", review='" + review + '\'' +
      ", reviewDate=" + reviewDate +
      ", volunteer=" + volunteer +
      ", institution=" + institution +
      '}';
  }

}