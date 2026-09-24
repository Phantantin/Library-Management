package com.zou.repository;

import com.zou.modal.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
org.springframework.data.domain.Page<User> findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name,String email,org.springframework.data.domain.Pageable page);
@org.springframework.data.jpa.repository.Query("select function('date', u.createdAt), count(u) from User u where u.createdAt >= :start group by function('date', u.createdAt) order by function('date', u.createdAt)")
java.util.List<Object[]> countCreatedByDaySince(@org.springframework.data.repository.query.Param("start") java.time.LocalDateTime start);
}
