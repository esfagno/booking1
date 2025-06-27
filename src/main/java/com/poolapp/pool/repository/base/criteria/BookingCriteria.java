package com.poolapp.pool.repository.base.criteria;

import com.poolapp.pool.model.Booking;
import com.poolapp.pool.model.enums.BookingStatus;
import com.poolapp.pool.repository.custom.BookingRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingCriteria implements BookingRepositoryCustom {

    private final EntityManager entityManager;


    @Override
    public List<Booking> findBookingsByFilter(String userEmail, BookingStatus status, String poolName, LocalDateTime startTime) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Booking> query = cb.createQuery(Booking.class);
        Root<Booking> bookingRoot = query.from(Booking.class);

        Join<Object, Object> sessionJoin = bookingRoot.join("session");
        Join<Object, Object> poolJoin = sessionJoin.join("pool");
        Join<Object, Object> userJoin = bookingRoot.join("user");

        List<Predicate> predicates = new ArrayList<>();

        if (userEmail != null && !userEmail.isBlank()) {
            predicates.add(cb.equal(cb.lower(userJoin.get("email")), userEmail.toLowerCase()));
        }

        if (status != null) {
            predicates.add(cb.equal(bookingRoot.get("status"), status));
        }

        if (poolName != null && !poolName.isBlank()) {
            predicates.add(cb.equal(cb.lower(poolJoin.get("name")), poolName.toLowerCase()));
        }

        if (startTime != null) {
            predicates.add(cb.equal(sessionJoin.get("startTime"), startTime));
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        return entityManager.createQuery(query).getResultList();
    }
}
