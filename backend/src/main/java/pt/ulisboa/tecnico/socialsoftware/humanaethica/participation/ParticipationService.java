package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.ACTIVITY_NOT_FOUND;

import org.springframework.beans.factory.annotation.Autowired;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.repository.ParticipationRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.repository.ActivityRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ParticipationService {
    @Autowired
    ActivityRepository activityRepository;
    @Autowired
    ParticipationRepository participationRepository;


    /*
     * Nestes testes deve ser verificado que o serviço devolve os valores corretos.
     *  Também deve ser testado que os valores de entrada são válidos: 
     *    existe um voluntário associado a volunteerId (que está definido em participationDto), 
     *    uma actividade associada a activityId e um objeto participationDto.
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ParticipationDto createParticipation(Integer activityId, ParticipationDto participationDto) {
        if (activityId == null) {
            throw new HEException(ACTIVITY_NOT_FOUND);
        } 
        
        Activity activity = activityRepository.findById(activityId).orElseThrow(() -> new HEException(ACTIVITY_NOT_FOUND, activityId));

        Participation participation = new Participation(participationDto, activity, participationDto.getVolunteer());

        participationRepository.save(participation);
        return new ParticipationDto(participation);
    }
}
