package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/participations")
public class ParticipationController {
    @Autowired
    private ParticipationService participationService;

    private static final Logger logger = LoggerFactory.getLogger(ParticipationController.class);

    @GetMapping
    public List<ParticipationDto> getParticipations() { return participationService.getParticipations(); }

    @PostMapping()
    @PreAuthorize("(hasRole('ROLE_MEMBER'))")
    public ParticipationDto createParticipation(Principal principal, @RequestBody ParticipationDto participationDto) {
        int volunteerId = ((AuthUser) ((Authentication) principal).getPrincipal()).getUser().getId();
        int activityId = participationDto.getActivity().getId();
        return participationService.createParticipation(volunteerId, activityId, participationDto);
    }
}
