package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.UserDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto;

public class AssessmentDto {

  private Integer id;
  private String review;
  private String reviewDate;
  private UserDto volunteer;
  private InstitutionDto institution;

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

}