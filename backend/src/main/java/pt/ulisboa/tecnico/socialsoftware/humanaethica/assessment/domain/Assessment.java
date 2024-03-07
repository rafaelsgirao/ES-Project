package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain;

import java.time.LocalDateTime;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;
import jakarta.persistence.*;

@Entity
@Table(name = "assessments")
public class Assessment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String review;

    private LocalDateTime reviewDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public LocalDateTime getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDateTime reviewDate) {
        this.reviewDate = reviewDate;
    }


}