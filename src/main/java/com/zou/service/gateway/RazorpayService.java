package com.zou.service.gateway;
import com.razorpay.RazorpayClient;
import com.zou.modal.Payment;
import com.zou.modal.User;
import com.zou.payload.response.PaymentLinkResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
@Service
public class RazorpayService {
    @Value("${razorpay.key.id:}") private String keyId;
    @Value("${razorpay.key.secret:}") private String keySecret;
    @Value("${app.frontend-url:http://localhost:3000}") private String frontend;
    private RazorpayClient client() throws Exception {
        if(keyId.isBlank() || keySecret.isBlank()) throw new IllegalStateException("Payment gateway is not configured");
        return new RazorpayClient(keyId.trim(),keySecret.trim());
    }
    public PaymentLinkResponse createPaymentLink(User user, Payment payment) throws Exception {
        JSONObject request = new JSONObject();
        request.put("amount", payment.getAmount());
        request.put("currency", payment.getCurrency());
        request.put("description",payment.getDescription());
        request.put("reference_id",payment.getTransactionId());
        request.put("callback_url",frontend+"/payment-success/"+payment.getId());
        request.put("callback_method","get");
        request.put("customer",new JSONObject().put("name",user.getFullName()).put("email",user.getEmail()));
        request.put("notes",new JSONObject().put("paymentId",String.valueOf(payment.getId())).put("userId",String.valueOf(user.getId())));
        var link=client().paymentLink.create(request);
        return new PaymentLinkResponse(link.get("short_url"),link.get("id"));
    }
    public JSONObject fetchPaymentDetails(String id) throws Exception {
        if(id==null || !id.matches("pay_[A-Za-z0-9]+")) throw new IllegalArgumentException("Invalid payment reference");
        return client().payments.fetch(id).toJson();
    }
    public JSONObject fetchLink(String id) throws Exception {return client().paymentLink.fetch(id).toJson();}
}
