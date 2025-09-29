package com.morago.backend.repository;

import com.morago.backend.entity.Call;
import com.morago.backend.entity.enumFiles.CallStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CallRepository extends JpaRepository<Call, Long> {
    boolean existsByCaller_IdAndRecipient_IdAndTranslatorHasJoinedTrueAndEndCallTrueAndCallStatus(
            Long callerId, Long recipientId, CallStatus callStatus
    );

    Optional<Call> findTopByCaller_IdAndRecipient_IdAndTranslatorHasJoinedTrueAndEndCallTrueOrderByCreatedAtDesc(
            Long callerId, Long recipientId);

    List<Call> findByCallerIdOrRecipientIdOrderByCreatedAtDesc(Long callerId, Long recipientId);
}