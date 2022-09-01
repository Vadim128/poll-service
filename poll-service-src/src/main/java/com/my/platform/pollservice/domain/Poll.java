package com.my.platform.pollservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "poll")
public class Poll extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "cover", nullable = false)
    private String cover;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "announcement")
    private Integer announcement;

    @Column(name = "announcement_time")
    private OffsetDateTime announcementTime;

    @Column(name = "start_time")
    private OffsetDateTime startTime;

    @Column(name = "end_time")
    private OffsetDateTime endTime;

    private boolean draft;

    private boolean archived;

    @Column(name = "celebrity_id", nullable = false)
    private UUID celebrityId;

    @JsonIgnore
    @ToString.Exclude
    @OrderBy("number ASC")
    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers;

    public void setAnswers(List<Answer> answers) {
        answers.forEach(answer -> answer.setPoll(this));
        this.answers = answers;
    }

    public void addAnswer(Answer answer) {
        answer.setPoll(this);
        answers.add(answer);
    }
}
