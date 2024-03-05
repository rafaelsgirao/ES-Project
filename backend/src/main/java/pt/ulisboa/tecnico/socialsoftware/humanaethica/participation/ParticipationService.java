package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.ACTIVITY_NOT_FOUND;
import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.USER_NOT_FOUND;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.repository.ActivityRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.repository.ParticipationRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.UserRepository;

import java.util.List;

@Service
public class ParticipationService {
  @Autowired ActivityRepository activityRepository;
  @Autowired ParticipationRepository participationRepository;
  @Autowired UserRepository userRepository;

  public List<ParticipationDto> getParticipations() {
    return participationRepository.findAll().stream()
            .map(participation -> new ParticipationDto(participation))
            .sorted()
            .toList();
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public ParticipationDto createParticipation(
      Integer activityId, Integer volunteerId, ParticipationDto participationDto) {
    if (activityId == null) {
      throw new HEException(ACTIVITY_NOT_FOUND);
    }
    if (volunteerId == null) {
      throw new HEException(USER_NOT_FOUND);
    }
    Volunteer volunteer =
        (Volunteer)
            userRepository
                .findById(volunteerId)
                .orElseThrow(() -> new HEException(USER_NOT_FOUND, volunteerId));
    Activity activity =
        activityRepository
            .findById(activityId)
            .orElseThrow(() -> new HEException(ACTIVITY_NOT_FOUND, activityId));

    Participation participation = new Participation(participationDto, activity, volunteer);

    participationRepository.save(participation);
    return new ParticipationDto(participation);
  }
}