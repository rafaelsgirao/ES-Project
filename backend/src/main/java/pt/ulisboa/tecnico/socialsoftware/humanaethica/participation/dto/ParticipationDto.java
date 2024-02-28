package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto;

public class ParticipationDto {

  public ParticipationDto() {}

  public ParticipationDto(Participation participation) {
    setId(activity.getId());
    setActivity(participation.getActivity());
    setAcceptanceDate(participation.getAcceptanceDate());
  }
}
