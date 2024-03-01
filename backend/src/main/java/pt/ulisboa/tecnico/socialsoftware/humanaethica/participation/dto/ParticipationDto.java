package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto;

import java.time.LocalDateTime;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;

public class ParticipationDto {
  private Integer id;
  private int rating;
  private LocalDateTime acceptanceDate;
  private Activity activity;
  private Volunteer volunteer;

  public ParticipationDto() {}

  public ParticipationDto(Participation participation) {
    setId(activity.getId());
    setActivity(participation.getActivity());
    setAcceptanceDate(participation.getAcceptanceDate());
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

  public void setActivity(Activity activity) {
    this.activity = activity;
  }

  public Volunteer getVolunteer() {
    return volunteer;
  }

  public void setVolunteer(Volunteer volunteer) {
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
