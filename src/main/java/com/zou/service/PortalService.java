package com.zou.service;
import com.zou.domain.*;
import com.zou.mapper.*;
import com.zou.payload.dto.*;
import com.zou.payload.request.ProfileRequest;
import com.zou.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class PortalService {
    private final UserService current;
    private final UserRepository users;
    private final BookReviewRepository reviews;
    private final BookReviewMapper reviewMapper;
    private final PaymentRepository payments;
    private final PaymentMapper paymentMapper;
    private final SubscriptionRepository subscriptions;
    private final SubscriptionMapper subscriptionMapper;
    private final BookLoanRepository loans;
    private final BookRepository books;
    private final ReservationRepository reservations;
    private final FineRepository fines;
    public Page<BookReviewDTO> reviews(boolean mine, Pageable page) throws Exception {
        return (mine ? reviews.findByUserId(current.getCurrentUser().getId(),page) : reviews.findAll(page)).map(reviewMapper::toDTO);
    }
    public Page<PaymentDTO> payments(Pageable page) throws Exception {return payments.findByUserId(current.getCurrentUser().getId(),page).map(paymentMapper::toDTO);}
    public Page<SubscriptionDTO> subscriptions(Pageable page) throws Exception {return subscriptions.findByUserId(current.getCurrentUser().getId(),page).map(subscriptionMapper::toDTO);}
    public Page<UserDTO> users(String search, Pageable page) {return users.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(search,search,page).map(UserMapper::toDTO);}
    public UserDTO user(Long id) throws Exception {return UserMapper.toDTO(current.findById(id));}
    @Transactional public UserDTO profile(ProfileRequest request) throws Exception {
        var user=current.getCurrentUser();user.setFullName(request.fullName());user.setPhone(request.phone());return UserMapper.toDTO(users.save(user));
    }
    public Map<String,Object> statistics() {
        Map<String,Object> data=new LinkedHashMap<>();
        data.put("totalActiveBooks",books.countByActiveTrue());data.put("totalAvailableBooks",books.countAvailableBooks());
        data.put("users",users.count());data.put("loans",loans.count());data.put("overdue",loans.findOverdueBookLoans(LocalDate.now(),PageRequest.of(0,1)).getTotalElements());
        data.put("reservations",reservations.count());data.put("payments",payments.count());
        data.put("activeSubscriptions",subscriptions.countValid(LocalDate.now()));
        data.put("loanStatuses",Arrays.stream(BookLoanStatus.values()).map(s->Map.of("name",s.name(),"count",loans.countByStatus(s))).toList());
        LocalDate start=LocalDate.now().minusDays(29);
        Map<LocalDate,Map<String,Object>> operations=new TreeMap<>();
        for(int offset=0;offset<30;offset++) {
            LocalDate day=start.plusDays(offset);
            Map<String,Object> point=new LinkedHashMap<>();point.put("date",day.toString());point.put("loans",0L);point.put("returns",0L);point.put("newUsers",0L);
            operations.put(day,point);
        }
        loans.countCheckoutsByDaySince(start).forEach(row->operations.get((LocalDate)row[0]).put("loans",((Number)row[1]).longValue()));
        loans.countReturnsByDaySince(start).forEach(row->operations.get((LocalDate)row[0]).put("returns",((Number)row[1]).longValue()));
        users.countCreatedByDaySince(start.atStartOfDay()).forEach(row->{LocalDate day=toDate(row[0]);if(operations.containsKey(day))operations.get(day).put("newUsers",((Number)row[1]).longValue());});
        data.put("operationsTrend",new ArrayList<>(operations.values()));

        Map<String,Map<String,Object>> revenue=new LinkedHashMap<>();
        Map<String,Long> revenueTotals=new TreeMap<>();
        payments.successfulRevenueSince(start.atStartOfDay()).forEach(row->{
            LocalDate day=((LocalDateTime)row[0]).toLocalDate();String currency=Objects.toString(row[1],"VND");long amount=((Number)row[2]).longValue();
            String key=day+"|"+currency;Map<String,Object> point=revenue.computeIfAbsent(key,k->{Map<String,Object> value=new LinkedHashMap<>();value.put("date",day.toString());value.put("currency",currency);value.put("amount",0L);return value;});
            point.put("amount",((Number)point.get("amount")).longValue()+amount);revenueTotals.merge(currency,amount,Long::sum);
        });
        data.put("revenueTrend",new ArrayList<>(revenue.values()));
        data.put("revenueTotals",revenueTotals.entrySet().stream().map(e->Map.of("currency",e.getKey(),"amount",e.getValue())).toList());
        data.put("popularBooks",loans.popularBookStatistics(PageRequest.of(0,5)).stream().map(row->Map.of("name",row[0],"count",row[1])).toList());
        data.put("popularGenres",loans.popularGenreStatistics(PageRequest.of(0,5)).stream().map(row->Map.of("name",row[0],"count",row[1])).toList());
        return data;
    }

    private LocalDate toDate(Object value) {
        if(value instanceof LocalDate date)return date;
        if(value instanceof java.sql.Date date)return date.toLocalDate();
        return ((LocalDateTime)value).toLocalDate();
    }
}
