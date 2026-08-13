package com.zou.service;

import com.zou.payload.dto.ReservationDTO;
import com.zou.payload.request.ReservationRequest;
import com.zou.payload.request.ReservationSearchRequest;
import com.zou.payload.response.PageResponse;
import org.springframework.data.domain.PageRequest;

public interface ReservationService {

    ReservationDTO createReservation(ReservationRequest reservationRequest) throws Exception;

    ReservationDTO createReservationForUser(ReservationRequest reservationRequest,
                                            Long userId) throws Exception;

    ReservationDTO cancelReservation(Long reservationId) throws Exception;
    ReservationDTO fulfillReservation(Long reservationId) throws Exception;

    PageResponse<ReservationDTO> getMyReservations(ReservationSearchRequest searchRequest) throws Exception;
    PageResponse<ReservationDTO> searchReservations(ReservationSearchRequest searchRequest);
}
