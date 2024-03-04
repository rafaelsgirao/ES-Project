package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.repository.ParticipationRepository;

import java.time.LocalDateTime;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;
import org.springframework.beans.factory.annotation.Autowired;

@Entity
@Table(name = "participation")
public class Participation  {
    @Autowired
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

    public Participation(Activity activity, Volunteer volunteer, ParticipationDto participationDto) {
        setRating(participationDto.getRating());
        setAcceptanceDate(DateHandler.now());
        _setActivity(activity);
        _setVolunteer(volunteer);

        verifyInvariants();

        setActivity(activity);
        setVolunteer(volunteer);
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
        activity.addParticipation(this);
    }
    
    private void _setActivity(Activity activity) {
        this.activity = activity;
    }
    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
        volunteer.addParticipation(this);
    }

    private void _setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
    }

    public void verifyInvariants() {
        verifyUniqueParticipation();
        checkFullActivity();
        verifyParticipationAfterEnrollment();
    }

    public void verifyUniqueParticipation() {
        volunteer.getParticipations().stream().forEach(participation -> {
            if (participation.getActivity().equals(this.activity) && !participation.equals(this)) {
                throw new HEException(DUPLICATE_PARTICIPATION);
            }
        });
    }

    public void checkFullActivity() {
        if (this.getActivity().getParticipantsNumberLimit() == this.getActivity().getParticipations().size()) {
            throw new HEException(ACTIVITY_FULL);
        }
    }

    public void verifyParticipationAfterEnrollment() {
        if (this.getAcceptanceDate().isBefore(this.getActivity().getApplicationDeadline())) {
            throw new HEException(ErrorMessage.ENROLLMENT_PERIOD_NOT_ENDED);
        }
    }

}