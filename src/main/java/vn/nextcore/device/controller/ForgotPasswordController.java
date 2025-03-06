package vn.nextcore.device.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.nextcore.device.dto.req.ChangePasswordRequest;
import vn.nextcore.device.dto.resp.DataResponse;
import vn.nextcore.device.dto.resp.ReTokenResponse;
import vn.nextcore.device.service.forgotPassword.IForgotPasswordService;

@RestController
@RequestMapping("/api/forgotPassword")
@Validated
public class ForgotPasswordController {
    @Autowired
    private IForgotPasswordService forgotPasswordService;

    @PostMapping("/verifyMail/{email}")
    @ResponseStatus(HttpStatus.OK)
    public DataResponse<String> verifyEmail(@PathVariable String email) {
        String result = forgotPasswordService.verifyEmail(email);
        return new DataResponse<>(result);
    }

    @PostMapping("/verifyOtp/{otp}/{email}")
    @ResponseStatus(HttpStatus.OK)
    public DataResponse<ReTokenResponse> verifyOtp(@PathVariable String otp, @PathVariable String email) {
        ReTokenResponse result = forgotPasswordService.verifyOtp(otp, email);
        return new DataResponse<>(result);
    }

    @PostMapping("/changePassword")
    public DataResponse<String> changePassword(
            HttpServletRequest request,
            @Valid @RequestBody ChangePasswordRequest passwordRequest) {
        String result = forgotPasswordService.changePassword(request, passwordRequest);
        return new DataResponse<>(result);
    }
}
