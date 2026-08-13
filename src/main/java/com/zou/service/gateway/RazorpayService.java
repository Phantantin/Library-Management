//package com.zou.service.gateway;
//
//
//import com.razorpay.PaymentLink;
//import com.razorpay.RazorpayClient;
//import com.razorpay.RazorpayException;
//import com.zou.domain.PaymentType;
//import com.zou.modal.Payment;
//import com.zou.modal.SubscriptionPlan;
//import com.zou.modal.User;
//import com.zou.payload.response.PaymentLinkResponse;
//import com.zou.service.SubscriptionPlanService;
//import lombok.RequiredArgsConstructor;
//import org.json.JSONObject;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//
//@Service
//@RequiredArgsConstructor
//public class RazorpayService {
//
//    private final SubscriptionPlanService subscriptionPlanService;
//    @Value("${razorpay.key.id:}")
//    private String razorpayKeyId;
//
//    @Value("${razorpay.key.secret:}")
//    private String razorpayKeySecret;
//
//    @Value("${razorpay.callback.base-url:http://localhost:5173}")
//    private String callbackBaseUrl;
//
////    public PaymentLinkResponse createPaymentLink(User user, Payment payment){
////
////        try{
////            RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
////            Long amountInPaisa = payment.getAmount()*100;
////
////            JSONObject paymentLinkRequest = new JSONObject();
////            paymentLinkRequest.put("amount", amountInPaisa);
////            paymentLinkRequest.put("currency", "INR");
////            paymentLinkRequest.put("description", payment.getDescription());
////
////            JSONObject customer = new JSONObject();
////            customer.put("name", user.getFullName());
////            customer.put("email", user.getEmail());
////            if(user.getPhone() !=null){
////                customer.put("contact", user.getPhone());
////            }
////
////            paymentLinkRequest.put("customer", customer);
////
////            JSONObject notify = new JSONObject();
////            notify.put("email", true);
////            notify.put("sms", user.getPhone() !=null);
////            paymentLinkRequest.put("notify", notify);
////
////            paymentLinkRequest.put("reminder_enable", true);
////
////
////            // callback
////            String successUrl = callbackBaseUrl + "/payment-success/" + payment.getId();
////
////            paymentLinkRequest.put("callback_url", successUrl);
////            paymentLinkRequest.put("callback_method", "get");
////
////            JSONObject notes = new JSONObject();
////            notes.put("user_id", user.getId());
////            notes.put("payment_id", payment.getId());
////
////            if(payment.getPaymentType() == PaymentType.MEMBERSHIP){
////                notes.put("subscription_id", payment.getSubscription().getId());
////                notes.put("plan", payment.getSubscription().getPlan().getPlanCode());
////                notes.put("type", PaymentType.MEMBERSHIP);
////            }else if(payment.getPaymentType()== PaymentType.FINE){
////
////                notes.put("type", PaymentType.FINE);
////            }
////
////            paymentLinkRequest.put("notes", notes);
////            PaymentLink paymentLink = razorpayClient.paymentLink.create(paymentLinkRequest);
////
////            String paymentUrl = paymentLink.get("short_url");
////            String paymentLinkId = paymentLink.get("id");
////
////            PaymentLinkResponse response = new PaymentLinkResponse();
////            response.setPayment_link_url(paymentUrl);
////            response.setPayment_link_id(paymentLinkId);
////
////            return response;
////
////        }catch (RazorpayException e){
////            throw  new RuntimeException(e);
////        }
////
////    }
//
//
//    public PaymentLinkResponse createPaymentLink(
//            User user,
//            Payment payment
//    ) {
//        try {
//            RazorpayClient razorpayClient =
//                    new RazorpayClient(
//                            razorpayKeyId.trim(),
//                            razorpayKeySecret.trim()
//                    );
//
//            if (payment.getAmount() == null
//                    || payment.getAmount() <= 0) {
//                throw new IllegalArgumentException(
//                        "Payment amount must be greater than 0"
//                );
//            }
//
//            /*
//             * payment.getAmount() phải đang lưu theo RUPEE.
//             * Ví dụ 100 nghĩa là ₹100.
//             */
//            long amountInPaisa = Math.multiplyExact(
//                    payment.getAmount(),
//                    100L
//            );
//
//            System.out.println(
//                    "Payment amount in rupees: " + payment.getAmount()
//            );
//            System.out.println(
//                    "Amount sent in paise: " + amountInPaisa
//            );
//
//            JSONObject paymentLinkRequest = new JSONObject();
//
//            paymentLinkRequest.put("amount", amountInPaisa);
//            paymentLinkRequest.put("currency", "INR");
//            paymentLinkRequest.put(
//                    "description",
//                    payment.getDescription()
//            );
//
//            JSONObject customer = new JSONObject();
//            customer.put("name", user.getFullName());
//            customer.put("email", user.getEmail());
//
//            if (user.getPhone() != null
//                    && !user.getPhone().isBlank()) {
//                customer.put("contact", user.getPhone());
//            }
//
//            paymentLinkRequest.put("customer", customer);
//
//            JSONObject notify = new JSONObject();
//            notify.put("email", true);
//            notify.put(
//                    "sms",
//                    user.getPhone() != null
//                            && !user.getPhone().isBlank()
//            );
//
//            paymentLinkRequest.put("notify", notify);
//            paymentLinkRequest.put("reminder_enable", true);
//
//            String successUrl =
//                    callbackBaseUrl
//                            + "/payment-success/"
//                            + payment.getId();
//
//            paymentLinkRequest.put("callback_url", successUrl);
//            paymentLinkRequest.put("callback_method", "get");
//
//            JSONObject notes = new JSONObject();
//
//            // Phải thống nhất tên với verifyPayment()
//            notes.put("userId", user.getId());
//            notes.put("paymentId", payment.getId());
//
//            if (payment.getPaymentType() == PaymentType.MEMBERSHIP) {
//                notes.put(
//                        "subscriptionId",
//                        payment.getSubscription().getId()
//                );
//                notes.put(
//                        "plan",
//                        payment.getSubscription()
//                                .getPlan()
//                                .getPlanCode()
//                );
//                notes.put(
//                        "type",
//                        PaymentType.MEMBERSHIP.name()
//                );
//            } else if (payment.getPaymentType() == PaymentType.FINE) {
//                notes.put(
//                        "type",
//                        PaymentType.FINE.name()
//                );
//            }
//
//            paymentLinkRequest.put("notes", notes);
//
//            System.out.println(
//                    "Razorpay request: " + paymentLinkRequest
//            );
//
//            PaymentLink paymentLink =
//                    razorpayClient.paymentLink.create(
//                            paymentLinkRequest
//                    );
//
//            return PaymentLinkResponse.builder()
//                    .payment_link_url(
//                            paymentLink.get("short_url")
//                    )
//                    .payment_link_id(
//                            paymentLink.get("id")
//                    )
//                    .build();
//
//        } catch (ArithmeticException e) {
//            throw new RuntimeException(
//                    "Payment amount is too large: "
//                            + payment.getAmount(),
//                    e
//            );
//        } catch (RazorpayException e) {
//            throw new RuntimeException(
//                    "Failed to create Razorpay payment link: "
//                            + e.getMessage(),
//                    e
//            );
//        }
//    }
//
//    public JSONObject fetchPaymentDetails(String paymentId) throws Exception{
//
//        try{
//            RazorpayClient razorpay = new RazorpayClient(razorpayKeyId,  razorpayKeySecret);
//            com.razorpay.Payment payment = razorpay.payments.fetch(paymentId);
//
//            return payment.toJson();
//        }catch (RazorpayException e){
//            throw  new Exception("Faild to fetch payment details" + e.getMessage(), e);
//        }
//    }
//
//    public boolean isValidPayment(String paymentId){
//
//        try{
//            JSONObject paymentDetails = fetchPaymentDetails(paymentId);
//            String status = paymentDetails.optString("status");
//            long amount = paymentDetails.optLong("amount");
//            long amountInRupees = amount/100;
//
//            JSONObject notes = paymentDetails.optJSONObject("notes");
//
//            String paymentType= notes.getString("type");
//
//            // check status
//            if(!"captured".equalsIgnoreCase(status)){
//                return false;
//            }
//
//            // check expected amount
//            if(paymentType.equals(PaymentType.MEMBERSHIP.toString())){
//                String planCode = notes.optString("plan");
//                SubscriptionPlan subscriptionPlan = subscriptionPlanService
//                        .getBySubscriptionPlanCode(planCode);
//                return amountInRupees == subscriptionPlan.getPrice();
//            }else if(paymentType.equals(PaymentType.FINE.toString())){
//                Long findId = notes.getLong("id");
//                //todo
//            }
//            return false;
//
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//}











package com.zou.service.gateway;

import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.zou.domain.PaymentType;
import com.zou.modal.Payment;
import com.zou.modal.SubscriptionPlan;
import com.zou.modal.User;
import com.zou.payload.response.PaymentLinkResponse;
import com.zou.service.SubscriptionPlanService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RazorpayService {

    private final SubscriptionPlanService subscriptionPlanService;

    // VND có exponent = 0
    private static final String PAYMENT_CURRENCY = "VND";

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Value("${razorpay.callback.base-url:http://localhost:5173}")
    private String callbackBaseUrl;


    // =========================================================
    // CREATE PAYMENT LINK
    // =========================================================
    public PaymentLinkResponse createPaymentLink(
            User user,
            Payment payment
    ) {

        try {

            RazorpayClient razorpayClient =
                    new RazorpayClient(
                            razorpayKeyId.trim(),
                            razorpayKeySecret.trim()
                    );

            // =========================
            // Validate amount
            // =========================
            if (payment.getAmount() == null
                    || payment.getAmount() <= 0) {

                throw new IllegalArgumentException(
                        "Payment amount must be greater than 0"
                );
            }


            /*
             * ==========================================
             * VND là zero-decimal currency.
             *
             * Database:
             *      99000 = 99.000 VND
             *
             * Gửi Razorpay:
             *      amount = 99000
             *
             * KHÔNG nhân 100
             * ==========================================
             */
            long amountInVnd = payment.getAmount();


            // =========================
            // Payment Link Request
            // =========================
            JSONObject paymentLinkRequest =
                    new JSONObject();

            paymentLinkRequest.put(
                    "amount",
                    amountInVnd
            );

            paymentLinkRequest.put(
                    "currency",
                    PAYMENT_CURRENCY
            );

            if (payment.getDescription() != null) {

                paymentLinkRequest.put(
                        "description",
                        payment.getDescription()
                );
            }


            // =========================
            // Customer
            // =========================
            JSONObject customer =
                    new JSONObject();

            if (user.getFullName() != null
                    && !user.getFullName().isBlank()) {

                customer.put(
                        "name",
                        user.getFullName()
                );
            }

            if (user.getEmail() != null
                    && !user.getEmail().isBlank()) {

                customer.put(
                        "email",
                        user.getEmail()
                );
            }

            if (user.getPhone() != null
                    && !user.getPhone().isBlank()) {

                customer.put(
                        "contact",
                        user.getPhone()
                );
            }

            paymentLinkRequest.put(
                    "customer",
                    customer
            );


            // =========================
            // Notification
            // =========================
            JSONObject notify =
                    new JSONObject();

            notify.put(
                    "email",
                    user.getEmail() != null
                            && !user.getEmail().isBlank()
            );

            notify.put(
                    "sms",
                    user.getPhone() != null
                            && !user.getPhone().isBlank()
            );

            paymentLinkRequest.put(
                    "notify",
                    notify
            );

            paymentLinkRequest.put(
                    "reminder_enable",
                    true
            );


            // =========================
            // Reference ID
            // =========================
            paymentLinkRequest.put(
                    "reference_id",
                    "PAYMENT_" + payment.getId()
            );


            // =========================
            // Callback URL
            // =========================
            String successUrl =
                    callbackBaseUrl
                            + "/payment-success/"
                            + payment.getId();

            paymentLinkRequest.put(
                    "callback_url",
                    successUrl
            );

            paymentLinkRequest.put(
                    "callback_method",
                    "get"
            );


            // =========================
            // Notes
            // =========================
            JSONObject notes =
                    new JSONObject();

            /*
             * Dùng String để khi đọc lại bằng optString()
             * không bị lệch kiểu dữ liệu.
             */
            notes.put(
                    "userId",
                    String.valueOf(user.getId())
            );

            notes.put(
                    "paymentId",
                    String.valueOf(payment.getId())
            );

            notes.put(
                    "type",
                    payment.getPaymentType().name()
            );


            // =========================
            // MEMBERSHIP
            // =========================
            if (payment.getPaymentType()
                    == PaymentType.MEMBERSHIP) {

                if (payment.getSubscription() == null) {

                    throw new IllegalStateException(
                            "Subscription is required for membership payment"
                    );
                }

                notes.put(
                        "subscriptionId",
                        String.valueOf(
                                payment.getSubscription().getId()
                        )
                );

                if (payment.getSubscription().getPlan() != null) {

                    notes.put(
                            "plan",
                            payment.getSubscription()
                                    .getPlan()
                                    .getPlanCode()
                    );
                }
            }


            // =========================
            // FINE
            // =========================
            else if (payment.getPaymentType()
                    == PaymentType.FINE) {

                notes.put(
                        "type",
                        PaymentType.FINE.name()
                );

                /*
                 * Nếu Payment của bạn có fine:
                 *
                 * if (payment.getFine() != null) {
                 *     notes.put(
                 *         "fineId",
                 *         String.valueOf(
                 *             payment.getFine().getId()
                 *         )
                 *     );
                 * }
                 */
            }


            paymentLinkRequest.put(
                    "notes",
                    notes
            );


            // =========================
            // Log để test
            // =========================
            System.out.println(
                    "======================================"
            );

            System.out.println(
                    "Payment amount DB: "
                            + payment.getAmount()
            );

            System.out.println(
                    "Amount sent to Razorpay: "
                            + amountInVnd
            );

            System.out.println(
                    "Currency: "
                            + PAYMENT_CURRENCY
            );

            System.out.println(
                    "Callback URL: "
                            + successUrl
            );

            System.out.println(
                    "Razorpay request: "
                            + paymentLinkRequest
            );

            System.out.println(
                    "======================================"
            );


            // =========================
            // Create Razorpay Link
            // =========================
            PaymentLink paymentLink =
                    razorpayClient
                            .paymentLink
                            .create(
                                    paymentLinkRequest
                            );


            String paymentUrl =
                    paymentLink.get("short_url");

            String paymentLinkId =
                    paymentLink.get("id");


            // =========================
            // Response
            // =========================
            return PaymentLinkResponse
                    .builder()
                    .payment_link_url(
                            paymentUrl
                    )
                    .payment_link_id(
                            paymentLinkId
                    )
                    .build();


        } catch (RazorpayException e) {

            throw new RuntimeException(
                    "Failed to create Razorpay payment link: "
                            + e.getMessage(),
                    e
            );
        }
    }


    // =========================================================
    // FETCH PAYMENT DETAILS
    // =========================================================
    public JSONObject fetchPaymentDetails(
            String paymentId
    ) throws Exception {

        if (paymentId == null
                || paymentId.isBlank()) {

            throw new IllegalArgumentException(
                    "Razorpay payment ID is required"
            );
        }

        try {

            RazorpayClient razorpay =
                    new RazorpayClient(
                            razorpayKeyId.trim(),
                            razorpayKeySecret.trim()
                    );

            com.razorpay.Payment payment =
                    razorpay.payments.fetch(
                            paymentId
                    );

            return payment.toJson();

        } catch (RazorpayException e) {

            throw new Exception(
                    "Failed to fetch Razorpay payment details: "
                            + e.getMessage(),
                    e
            );
        }
    }


    // =========================================================
    // VALIDATE PAYMENT
    // =========================================================
    public boolean isValidPayment(
            String paymentId
    ) {

        try {

            JSONObject paymentDetails =
                    fetchPaymentDetails(
                            paymentId
                    );


            // =========================
            // Status
            // =========================
            String status =
                    paymentDetails.optString(
                            "status"
                    );


            if (!"captured"
                    .equalsIgnoreCase(status)) {

                System.out.println(
                        "Payment is not captured. Status: "
                                + status
                );

                return false;
            }


            // =========================
            // Currency
            // =========================
            String currency =
                    paymentDetails.optString(
                            "currency"
                    );


            if (!PAYMENT_CURRENCY
                    .equalsIgnoreCase(currency)) {

                System.out.println(
                        "Invalid currency. Expected VND but received: "
                                + currency
                );

                return false;
            }


            // =========================
            // Amount
            // =========================
            /*
             * VND exponent = 0
             *
             * Razorpay amount:
             * 99000
             *
             * DB:
             * 99000
             *
             * Không chia 100.
             */
            long paidAmount =
                    paymentDetails.optLong(
                            "amount"
                    );


            // =========================
            // Notes
            // =========================
            JSONObject notes =
                    paymentDetails.optJSONObject(
                            "notes"
                    );


            if (notes == null) {

                System.out.println(
                        "Payment notes not found"
                );

                return false;
            }


            String paymentType =
                    notes.optString(
                            "type"
                    );


            if (paymentType == null
                    || paymentType.isBlank()) {

                System.out.println(
                        "Payment type not found in notes"
                );

                return false;
            }


            // =========================
            // MEMBERSHIP
            // =========================
            if (PaymentType.MEMBERSHIP
                    .name()
                    .equals(paymentType)) {

                String planCode =
                        notes.optString(
                                "plan"
                        );


                if (planCode == null
                        || planCode.isBlank()) {

                    System.out.println(
                            "Plan code not found"
                    );

                    return false;
                }


                SubscriptionPlan subscriptionPlan =
                        subscriptionPlanService
                                .getBySubscriptionPlanCode(
                                        planCode
                                );


                if (subscriptionPlan == null) {

                    System.out.println(
                            "Subscription plan not found"
                    );

                    return false;
                }


                long expectedAmount =
                        subscriptionPlan.getPrice();


                System.out.println(
                        "Paid amount: "
                                + paidAmount
                );

                System.out.println(
                        "Expected amount: "
                                + expectedAmount
                );


                return paidAmount
                        == expectedAmount;
            }


            // =========================
            // FINE
            // =========================
            else if (PaymentType.FINE
                    .name()
                    .equals(paymentType)) {

                /*
                 * TODO:
                 * Khi làm payment tiền phạt,
                 * lấy fineId từ notes rồi kiểm tra
                 * số tiền thực tế.
                 */

                System.out.println(
                        "Fine payment verification is not implemented yet"
                );

                return false;
            }


            return false;


        } catch (Exception e) {

            System.out.println(
                    "Payment validation failed: "
                            + e.getMessage()
            );

            e.printStackTrace();

            return false;
        }
    }
}