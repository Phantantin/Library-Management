package com.zou.service;

import com.zou.domain.FineStatus;
import com.zou.domain.FineType;
import com.zou.payload.dto.FineDTO;
import com.zou.payload.request.CreateFineRequest;
import com.zou.payload.request.WaiveFineRequest;
import com.zou.payload.response.PageResponse;
import com.zou.payload.response.PaymentInitiateResponse;

import java.util.List;

public interface FineService {

    FineDTO createFine(CreateFineRequest createFineRequest) throws Exception;

    PaymentInitiateResponse payFine(Long fineId, String transactionId) throws Exception;

    void markFineAsPaid(Long fineId, Long amount, String transactionId) throws Exception;

    FineDTO waiveFine(WaiveFineRequest waiveFineRequest) throws Exception;

    List<FineDTO> getMyFines(FineStatus status, FineType type) throws Exception;

    PageResponse<FineDTO> getAllFines(
            FineStatus status,
            FineType type,
            Long userId,
            int page,
            int size
    );
}
