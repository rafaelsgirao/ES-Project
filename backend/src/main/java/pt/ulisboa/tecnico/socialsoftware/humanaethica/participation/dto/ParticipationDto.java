package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto;

import java.time.LocalDateTime;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.UserDto;

public class ParticipationDto {
    private Integer id;
    private Integer rating;
    private LocalDateTime acceptanceDate;
    private ActivityDto activity;
    private UserDto volunteer;

    public ParticipationDto() {}

    public ParticipationDto(Participation participation) {
        setId(participation.getId());
        setRating(participation.getRating());
        setAcceptanceDate(participation.getAcceptanceDate());
        setVolunteer(new UserDto(participation.getVolunteer()));
        setActivity(new ActivityDto(participation.getActivity(), true));
    }

    public void setId(Integer id) {
        this.id = id;
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

    public ActivityDto getActivity() {
        return activity;
    }

    public void setActivity(ActivityDto activity) {
        this.activity = activity;
    }

    public UserDto getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(UserDto volunteer) {
        this.volunteer = volunteer;
    }

    public String toString() {
        return "ParticipationDto{" +
                "id=" + id +
                ", rating=" + rating +
                ", acceptanceDate=" + acceptanceDate +
                ", activity=" + activity +
                ", volunteer=" +volunteer +
                "}";
    }
}
