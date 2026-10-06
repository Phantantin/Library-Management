package com.zou.repository;

import com.zou.modal.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    org.springframework.data.domain.Page<Payment> findByUserId(Long id, org.springframework.data.domain.Pageable pageable);
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select p from Payment p where p.id=:id")
    java.util.Optional<Payment> findLockedById(@org.springframework.data.repository.query.Param("id") Long id);

    java.util.Optional<Payment> findByGatewayOrderId(String gatewayOrderId);

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select p from Payment p where p.gatewayOrderId=:gatewayOrderId")
    java.util.Optional<Payment> findLockedByGatewayOrderId(@org.springframework.data.repository.query.Param("gatewayOrderId") String gatewayOrderId);

    @org.springframework.data.jpa.repository.Query("select p.completedAt, p.currency, p.amount from Payment p where p.status = com.zou.domain.PaymentStatus.SUCCESS and p.completedAt >= :start order by p.completedAt")
    java.util.List<Object[]> successfulRevenueSince(@org.springframework.data.repository.query.Param("start") java.time.LocalDateTime start);

}
