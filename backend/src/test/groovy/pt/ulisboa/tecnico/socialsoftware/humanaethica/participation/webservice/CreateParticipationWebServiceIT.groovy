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
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.UserDto

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateParticipationWebServiceIT extends SpockTest {

    @LocalServerPort
    private int port

    def participationDto
    def activity
    def activityDto
    def volunteer1
    def volunteerDto1

    def setup() {
        deleteAll() // ensure a clean-slate database

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        def institution = institutionService.getDemoInstitution()

        volunteer1 = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)
        userRepository.save(volunteer1)
        volunteerDto1 = new UserDto(volunteer1)


        def themesDto = new ArrayList<ThemeDto>()
        activityDto = createActivityDto(ACTIVITY_NAME_1, ACTIVITY_REGION_1, 2, ACTIVITY_DESCRIPTION_1,
                ONE_DAY_AGO, IN_TWO_DAYS, IN_THREE_DAYS, themesDto)
        activity = new Activity(activityDto, institution, themesDto)
        activityRepository.save(activity)

        participationDto = createParticipationDto(PARTICIPATION_RATING_1, NOW, volunteer1, activity)
    }

    def "login as member, register a participation"() {
        given:
        demoMemberLogin()

        when:
        def response = webClient.post()
                .uri('/participations/' + activity.getId())
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(participationDto)
                .retrieve()
                .bodyToMono(ParticipationDto.class)
                .block()

        then: "check response data"
        response.rating == PARTICIPATION_RATING_1
        response.activity.getId() == activity.getId()
        response.volunteer.getId() == volunteer1.getId()

        and: "check database data"
        participationRepository.count() == 1
        def participation = participationRepository.findAll().get(0)
        participation.getActivity().getId() == activity.getId()
        participation.getActivity().getName() == ACTIVITY_NAME_1
        participation.getActivity().getRegion() == ACTIVITY_REGION_1
        participation.getActivity().participantsNumberLimit == 2
        participation.getActivity().getDescription() == ACTIVITY_DESCRIPTION_1
        participation.getActivity().getStartingDate().withNano(0) == IN_TWO_DAYS.withNano(0)
        participation.getActivity().getEndingDate().withNano(0) == IN_THREE_DAYS.withNano(0)
        participation.getActivity().getApplicationDeadline().withNano(0) == ONE_DAY_AGO.withNano(0)
        participation.getActivity().getThemes().size() == 0
        participation.getRating() == PARTICIPATION_RATING_1

        cleanup:
        deleteAll()
    }

    def "login as volunteer, register a participation with error"() {
        given:
        demoVolunteerLogin()

        when:
        webClient.post()
                .uri('/participations/' + activity.getId())
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(participationDto)
                .retrieve()
                .bodyToMono(ParticipationDto.class)
                .block()

        then: "an error is returned"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        participationRepository.count() == 0

        cleanup:
        deleteAll()
    }

    def "login as admin, register a participation"() {
            given:
            demoAdminLogin()

            when:
            webClient.post()
                    .uri('/participations/' + activity.getId())
                    .headers(httpHeaders -> httpHeaders.putAll(headers))
                    .bodyValue(participationDto)
                    .retrieve()
                    .bodyToMono(ParticipationDto.class)
                    .block()

            then: "an error is returned"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        participationRepository.count() == 0

            cleanup:
            deleteAll()
    }


}
