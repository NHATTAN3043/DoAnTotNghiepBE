package vn.nextcore.device.service.forgotPassword;

import jakarta.servlet.http.HttpServletRequest;
import vn.nextcore.device.dto.req.ChangePasswordRequest;
import vn.nextcore.device.dto.resp.ReTokenResponse;

public interface IForgotPasswordService {
    String verifyEmail(String email);

    ReTokenResponse verifyOtp(String otp, String email);

    String changePassword(HttpServletRequest request , ChangePasswordRequest passwordRequest);
}
