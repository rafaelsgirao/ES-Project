package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Valid;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto;
import java.util.List;

@RestController
@RequestMapping("/participations")
public class ParticipationController {
    @Autowired
    private ParticipationService participationService;



    @PostMapping("/{activityId}")
    @PreAuthorize("(hasRole('ROLE_MEMBER')) and hasPermission(#activityId, 'ACTIVITY.MEMBER')")
    public ParticipationDto createParticipation(@PathVariable Integer activityId, @Valid @RequestBody ParticipationDto participationDto) {
        return participationService.createParticipation(activityId, participationDto);  
    }

    @GetMapping(path={"/{activityId}"})
    @PreAuthorize("hasRole('ROLE_MEMBER') and hasPermission(#activityId, 'ACTIVITY.MEMBER')")
    public List<ParticipationDto> getParticipationsByActivity(@PathVariable Integer activityId) {
        return participationService.getParticipationsByActivity(activityId);
    }
}
