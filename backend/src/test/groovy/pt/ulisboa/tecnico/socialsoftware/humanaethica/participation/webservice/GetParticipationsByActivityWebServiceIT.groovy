package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.webservice

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.http.HttpStatus
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetParticipationsByActivityWebServiceIT extends SpockTest {

    @LocalServerPort
    private int port

    def institution
    def activity
    def volunteer1
    def volunteer2
    def participation1
    def participation2

    def setup() {
        deleteAll() // ensure a clean-slate database

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        and: "create necessary objects"
        institution = institutionService.getDemoInstitution()

        def activityDto = createActivityDto(ACTIVITY_NAME_1, ACTIVITY_REGION_1, ACTIVITY_LIMIT_2, ACTIVITY_DESCRIPTION_1,
                ONE_DAY_AGO,IN_TWO_DAYS,IN_THREE_DAYS, [])
        activity = new Activity(activityDto, institution, [])
        activityRepository.save(activity)
        volunteer1 = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)
        volunteer2 = new Volunteer(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)

        userRepository.save(volunteer1)
        userRepository.save(volunteer2)
        and: "create two participations"
        def participationDto1 = createParticipationDto(PARTICIPATION_RATING_1, NOW, (User) volunteer1, activity)
        def participationDto2 = createParticipationDto(PARTICIPATION_RATING_2, NOW, (User) volunteer2, activity)
        participation1 = new Participation(participationDto1, activity, volunteer1)
        participation2 = new Participation(participationDto2, activity, volunteer2)
        participationRepository.save(participation1)
        participationRepository.save(participation2)
    }

    def "login as member, list all activity participations"() {
        given:
        demoMemberLogin()

        when:
        def response = webClient.get()
                .uri('/participations/' + activity.getId())
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(List<ParticipationDto>.class)
                .block()
        then: "check response size"
        response.size() == 2
        def pDto1 = response.get(0)
        def pDto2 = response.get(1)

        and: "check participationDto's volunteer"
        pDto1.volunteer.name == USER_1_NAME
        pDto1.volunteer.username == USER_1_USERNAME
        pDto1.volunteer.email == USER_1_EMAIL
        pDto1.volunteer.state == User.State.APPROVED.toString()

        pDto2.volunteer.name == USER_2_NAME
        pDto2.volunteer.username == USER_2_USERNAME
        pDto2.volunteer.email == USER_2_EMAIL
        pDto2.volunteer.state == User.State.APPROVED.toString()

        and: "check participationDto's activity"
        //We knowingly don't check activity's dates.
        //Since they're strings in the returning result,
        // it's not practical to compare them to our known constants since we can't use .withNano(0) on them,as done elsewhere.
        // In a future sprint, a refactor could be made to use a fixed clock during testing (through dependency injection):
        // https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/time/Clock.html
        pDto1.activity.name == ACTIVITY_NAME_1
        pDto1.activity.region == ACTIVITY_REGION_1
        pDto1.activity.participantsNumberLimit == ACTIVITY_LIMIT_2
        pDto1.activity.description == ACTIVITY_DESCRIPTION_1
        pDto1.activity.institution.id == institution.id
        pDto1.activity.themes == []

        pDto2.activity.name == ACTIVITY_NAME_1
        pDto2.activity.region == ACTIVITY_REGION_1
        pDto2.activity.participantsNumberLimit == ACTIVITY_LIMIT_2
        pDto2.activity.description == ACTIVITY_DESCRIPTION_1
        pDto2.activity.institution.id == institution.id
        pDto2.activity.themes == []

        and: "check participationDto's rating"
        pDto1.rating == PARTICIPATION_RATING_1
        pDto2.rating == PARTICIPATION_RATING_2

        and: "ensure nothing else was written to database"
        participationRepository.count() == 2
        activityRepository.count() == 1
        userRepository.count() == 2 + 1 // demo member also exists
        cleanup:
        deleteAll()
    }

    def "login as volunteer, register a participation with error"() {
        given:
        demoVolunteerLogin()

        when:
        webClient.get()
                .uri('/participations/' + activity.getId())
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(List<ParticipationDto>.class)
                .block()

        then: "an error is returned"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN

        and: "ensure nothing else was written to database"
        participationRepository.count() == 2
        activityRepository.count() == 1
        userRepository.count() == 2 + 1 // demo member also exists
        cleanup:
        deleteAll()
    }

    def "login as admin, register a participation with error"() {
        given:
        demoAdminLogin()

        when:
        webClient.get()
                .uri('/participations/' + activity.getId())
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(List<ParticipationDto>.class)
                .block()

        then: "an error is returned"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN

        and: "ensure nothing else was written to database"
        participationRepository.count() == 2
        activityRepository.count() == 1
        userRepository.count() == 2 + 1 // demo member also exists
        cleanup:
        deleteAll()
    }

}

/*
Nestes testes deve ser verificado que o serviço devolve os valores corretos e que os resultados ficam na base de dados. Adicionalmente devem ser efetuados testes para verificar as condições de acesso.

Estes testes são de componente.
*/
