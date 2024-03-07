package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import spock.lang.Unroll
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException

@DataJpaTest
class CreateAssessmentServiceTest extends SpockTest {
  public static final String EXIST = "exist"
  public static final String NO_EXIST = "noExist"

  def volunteer
  def institution
  def activity

  def setup() {
    volunteer = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)
    userRepository.save(volunteer)


    institution = institutionService.getDemoInstitution()
    def activityDto = createActivityDto(ACTIVITY_NAME_1, ACTIVITY_REGION_1, 1, ACTIVITY_DESCRIPTION_1,
            TWO_DAYS_AGO, ONE_DAY_AGO, NOW, [])

    activity = new Activity(activityDto, institution, [])
    activityRepository.save(activity)
    
  
  }

  def "create assessment"() {
    given: "an assessment dto"
    def assessmentDto = createAssessmentDto(REVIEW_1, IN_ONE_DAY, institution, volunteer)

    when:
    def result = assessmentService.createAssessment(volunteer.getId(), institution.getId(), assessmentDto)

    then: "the returned data is correct"
    result.review == REVIEW_1
    result.institution.id == institution.id
    result.getVolunteer().getName() == USER_1_NAME
    and: "the assessment is saved in the database"
    assessmentRepository.findAll().size() == 1
    and: "the stored data is correct"
    def storedAssessment = assessmentRepository.findById(result.id).get()
    storedAssessment.review == REVIEW_1
    storedAssessment.institution.id == institution.id
    storedAssessment.volunteer.id == volunteer.id

  }


   @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
