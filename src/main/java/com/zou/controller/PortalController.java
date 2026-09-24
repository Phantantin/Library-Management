package com.zou.controller;
import com.zou.service.PortalService;
import com.zou.payload.dto.*;
import com.zou.payload.request.ProfileRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequiredArgsConstructor
public class PortalController {
    private final PortalService portal;
    private Pageable page(int page,int size) {return PageRequest.of(Math.max(0,page),Math.max(1,Math.min(size,100)),Sort.by("createdAt").descending());}
    @GetMapping("/api/reviews/my")
    public ResponseEntity<Page<BookReviewDTO>> myReviews(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size) throws Exception {return ResponseEntity.ok(portal.reviews(true,page(page,size)));}
    @GetMapping("/api/admin/reviews")
    public ResponseEntity<Page<BookReviewDTO>> reviews(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size) throws Exception {return ResponseEntity.ok(portal.reviews(false,page(page,size)));}
    @GetMapping("/api/payments/my")
    public ResponseEntity<Page<PaymentDTO>> payments(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size) throws Exception {return ResponseEntity.ok(portal.payments(page(page,size)));}
    @GetMapping("/api/subscriptions/my")
    public ResponseEntity<Page<SubscriptionDTO>> subscriptions(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size) throws Exception {return ResponseEntity.ok(portal.subscriptions(page(page,size)));}
    @GetMapping("/api/admin/users")
    public ResponseEntity<Page<UserDTO>> users(@RequestParam(defaultValue="") String search,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size) {return ResponseEntity.ok(portal.users(search,page(page,size)));}
    @GetMapping("/api/admin/users/{id}")
    public ResponseEntity<UserDTO> user(@PathVariable Long id) throws Exception {return ResponseEntity.ok(portal.user(id));}
    @PutMapping("/api/users/profile")
    public ResponseEntity<UserDTO> profile(@Valid @RequestBody ProfileRequest request) throws Exception {return ResponseEntity.ok(portal.profile(request));}
    @GetMapping("/api/admin/statistics")
    public ResponseEntity<Map<String,Object>> statistics() {return ResponseEntity.ok(portal.statistics());}
}
