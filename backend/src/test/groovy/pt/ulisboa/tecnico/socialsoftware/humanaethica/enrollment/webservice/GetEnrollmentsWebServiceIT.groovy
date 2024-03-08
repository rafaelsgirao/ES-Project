package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.webservice

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetEnrollmentsWebServiceIT extends SpockTest{

    @LocalServerPort
    private int port

    def setup(){
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        given: "a institution"
        def institution = institutionService.getDemoInstitution()
        and: "a volunteer"
        def volunteer = createMember(USER_1_NAME, USER_1_USERNAME, USER_1_PASSWORD, USER_1_EMAIL, Member.Type.VOLUNTEER, institution, Member.State.APPROVED)
        and: "another volunteer"
        def volunteer2 = createMember(USER_2_NAME, USER_2_USERNAME, USER_2_PASSWORD, USER_2_EMAIL, Member.Type.VOLUNTEER, institution, Member.State.APPROVED)
        and: "an activity"
        def activity = createActivity(ACTIVITY_NAME_1, IN_TWO_DAYS, IN_THREE_DAYS, IN_ONE_DAY)
        and: "enrollment info"
        def enrollmentDto = createEnrollmentDto(ENROLLMENT_MOTIVATION,NOW)
        and: 'an enrollment'
        def enrollment = new Enrollment(enrollmentDto, activity, volunteer)
        activityRepository.save(enrollment)
        and: 'another enrollment'
        enrollmentDto.motivation = ENROLLMENT_MOTIVATION_2
        enrollmentDto.enrollmentDate = NOW
        enrollment = new Enrollment(enrollmentDto, activity, volunteer2)
        activityRepository.save(enrollment)
    }

    def cleanup() {
        deleteAll()
    }

}