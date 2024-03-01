package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.repository.ParticipationRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

@Entity
@Table(name = "participation")
public class Participation  {
    @Transient
    private ParticipationRepository participationRepository;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int rating;

    @Column(name = "acceptance_date")
    private LocalDateTime acceptanceDate;

    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn(name = "activity_id")
    private Activity activity;

    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn(name = "volunteer_id")
    private Volunteer volunteer;

    public Participation() {
    }

    public Participation(ParticipationDto participationDto, Activity activity, Volunteer volunteer) {
        setRating(participationDto.getRating());
        setAcceptanceDate(DateHandler.now());
        setActivity(activity);
        setVolunteer(volunteer);

        verifyInvariants();
    }

    public Integer getId() {
        return id;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public LocalDateTime getAcceptanceDate() {
        return acceptanceDate;
    }

    public void setAcceptanceDate(LocalDateTime acceptanceDate) {
        this.acceptanceDate = acceptanceDate;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
    }

    public void verifyInvariants() {
        verifyUniqueParticipation();
        checkUniqueParticipation();
    }

    public void verifyUniqueParticipation() {
        int count = participationRepository.checkUniqueParticipation(volunteer.getId(), activity.getId());
        if (count > 0) {
            throw new HEException(DUPLICATE_PARTICIPATION);
        }
    }

    public void checkUniqueParticipation() {
        int count = participationRepository.countParticipations(activity.getId());
        if (count >= activity.getParticipantsNumberLimit()) {
            throw new HEException(ACTIVITY_FULL);
        }
    }
}