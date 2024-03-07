package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.UserDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto;

public class AssessmentDto {

  private Integer id;
  private String review;
  private String reviewDate;
  private UserDto volunteer;
  private InstitutionDto institution;


}