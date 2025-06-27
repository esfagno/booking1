package com.poolapp.pool.service;

import com.poolapp.pool.dto.SessionDTO;
import com.poolapp.pool.model.Session;

import java.time.LocalDateTime;
import java.util.List;

public interface SessionService {

    SessionDTO createSession(SessionDTO sessionDTO);

    void decrementSessionCapacity(SessionDTO sessionDTO);

    void incrementSessionCapacity(SessionDTO sessionDTO);

    List<SessionDTO> getAllSessions();

    boolean validateSessionHasAvailableSpots(SessionDTO sessionDTO);

    Session getSessionByPoolNameAndStartTime(String poolName, LocalDateTime startTime);


}

