package com.vijay.User_Master.service;

import com.vijay.User_Master.entity.DoctorVisit;
import com.vijay.User_Master.entity.VisitStatus;
import com.vijay.User_Master.exceptions.ResourceNotFoundException;
import com.vijay.User_Master.repository.DoctorVisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service for managing the lifecycle of doctor visits.
 * Implements state machine: CREATED → IN_PROGRESS → CLOSED → LOCKED
 */
@Service
@RequiredArgsConstructor
public class VisitLifecycleService {

    private final DoctorVisitRepository doctorVisitRepository;

    /**
     * Transition visit from CREATED to IN_PROGRESS
     */
    @Transactional
    public DoctorVisit startVisit(Long visitId) {
        DoctorVisit visit = getVisit(visitId);
        
        validateStateTransition(visit.getStatus(), VisitStatus.IN_PROGRESS);
        
        visit.setStatus(VisitStatus.IN_PROGRESS);
        return doctorVisitRepository.save(visit);
    }

    /**
     * Transition visit from IN_PROGRESS to CLOSED
     * Makes the visit read-only
     */
    @Transactional
    public DoctorVisit closeVisit(Long visitId, String closedBy) {
        DoctorVisit visit = getVisit(visitId);
        
        validateStateTransition(visit.getStatus(), VisitStatus.CLOSED);
        
        visit.setStatus(VisitStatus.CLOSED);
        visit.setClosedAt(LocalDateTime.now());
        visit.setClosedBy(closedBy);
        
        return doctorVisitRepository.save(visit);
    }

    /**
     * Transition visit from CLOSED to LOCKED
     * Permanently locks the visit (typically after 24 hours)
     */
    @Transactional
    public DoctorVisit lockVisit(Long visitId, String lockedBy) {
        DoctorVisit visit = getVisit(visitId);
        
        validateStateTransition(visit.getStatus(), VisitStatus.LOCKED);
        
        visit.setStatus(VisitStatus.LOCKED);
        visit.setLockedAt(LocalDateTime.now());
        visit.setLockedBy(lockedBy);
        
        return doctorVisitRepository.save(visit);
    }

    /**
     * Check if a visit can be edited
     * Only CREATED and IN_PROGRESS visits are editable
     */
    public boolean isEditable(DoctorVisit visit) {
        return visit.getStatus() == VisitStatus.CREATED 
            || visit.getStatus() == VisitStatus.IN_PROGRESS;
    }

    /**
     * Validate state transition is allowed
     */
    public void validateStateTransition(VisitStatus from, VisitStatus to) {
        boolean isValid = switch (from) {
            case CREATED -> to == VisitStatus.IN_PROGRESS;
            case IN_PROGRESS -> to == VisitStatus.CLOSED;
            case CLOSED -> to == VisitStatus.LOCKED;
            case LOCKED -> false; // No transitions from LOCKED
        };

        if (!isValid) {
            throw new IllegalStateException(
                String.format("Invalid state transition from %s to %s", from, to)
            );
        }
    }

    /**
     * Get visit by ID or throw exception
     */
    private DoctorVisit getVisit(Long visitId) {
        return doctorVisitRepository.findById(visitId)
            .orElseThrow(() -> new ResourceNotFoundException("DoctorVisit", "id", visitId));
    }

    /**
     * Check if visit is closed or locked
     */
    public boolean isImmutable(DoctorVisit visit) {
        return visit.getStatus() == VisitStatus.CLOSED 
            || visit.getStatus() == VisitStatus.LOCKED;
    }
}
