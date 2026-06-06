package com.backstopsolutions.morpheus.demo.filter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "meeting")
public class MeetingEntity {

    @Id
    private Long id;

    @Column(name = "strategy_id", nullable = false)
    private Long strategyId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "meeting_template_id", nullable = false)
    private MeetingTemplateEntity meetingTemplate;

    @Column(name = "partner_id")
    private Long partnerId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(Long strategyId) {
        this.strategyId = strategyId;
    }

    public MeetingTemplateEntity getMeetingTemplate() {
        return meetingTemplate;
    }

    public void setMeetingTemplate(MeetingTemplateEntity meetingTemplate) {
        this.meetingTemplate = meetingTemplate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
    }
}
