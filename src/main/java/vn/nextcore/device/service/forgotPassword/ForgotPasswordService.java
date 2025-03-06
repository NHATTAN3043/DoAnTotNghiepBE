package vn.nextcore.device.service.forgotPassword;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nextcore.device.dto.record.MailBody;
import vn.nextcore.device.dto.req.ChangePasswordRequest;
import vn.nextcore.device.dto.resp.ReTokenResponse;
import vn.nextcore.device.entity.ForgotPassword;
import vn.nextcore.device.entity.User;
import vn.nextcore.device.enums.ErrorCodeEnum;
import vn.nextcore.device.enums.PathEnum;
import vn.nextcore.device.exception.HandlerException;
import vn.nextcore.device.repository.ForgotPasswordRepository;
import vn.nextcore.device.repository.UserRepository;
import vn.nextcore.device.security.jwt.JwtUtil;
import vn.nextcore.device.service.email.EmailService;
import vn.nextcore.device.validation.HandlerValidateParams;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

@Service
public class ForgotPasswordService implements IForgotPasswordService{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ForgotPasswordRepository forgotPasswordRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private int temporaryTokenExp = 300000;

    @Override
    @Transactional
    public String verifyEmail(String email) {
        String message = "Email send for verification!";
        try {
            // validate email
            HandlerValidateParams.validateEmailFormat(email, ErrorCodeEnum.ER003);

            User user = userRepository.findByEmail(email);
            if (user == null) {
                throw new HandlerException(ErrorCodeEnum.ER108.getCode(), ErrorCodeEnum.ER108.getMessage(), HttpStatus.NOT_FOUND);
            }
            Integer otp = otpGenerator();
            MailBody mailBody = MailBody.builder()
                    .to(email)
                    .text("Your OTP is: " + otp + ". Please use this code to complete your verification.")
                    .subject("OTP for forgot password request")
                    .build();

            ForgotPassword forgotPassword = ForgotPassword.builder()
                    .otp(otp)
                    .expirationTime(new Date(System.currentTimeMillis() + 70 * 1000))
                    .user(user)
                    .build();

            // send otp mail
            emailService.sendSimpleMessage(mailBody);

            // save otp info
            ForgotPassword existsForgotPassword = forgotPasswordRepository.findByUser(user);
            if (existsForgotPassword != null) {
                existsForgotPassword.setOtp(otp);
                existsForgotPassword.setExpirationTime(new Date(System.currentTimeMillis() + 70 * 1000));
                forgotPasswordRepository.save(existsForgotPassword);
            } else {
                forgotPasswordRepository.save(forgotPassword);
            }

            return message;
        } catch (HandlerException handlerException) {
            handlerException.printStackTrace();
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), PathEnum.VERIFY_EMAIL_PATH.getPath(), handlerException.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.VERIFY_EMAIL_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ReTokenResponse verifyOtp(String otp, String email) {
        ReTokenResponse result = new ReTokenResponse();
        String message = "OTP verified!";
        try {
            // validate input
            HandlerValidateParams.validateOtpFormat(otp, ErrorCodeEnum.ER109);
            HandlerValidateParams.validateEmailFormat(email, ErrorCodeEnum.ER003);

            User userExists = userRepository.findByEmail(email);
            if (userExists == null) {
                throw new HandlerException(ErrorCodeEnum.ER108.getCode(), ErrorCodeEnum.ER108.getMessage(), HttpStatus.NOT_FOUND);
            }

            ForgotPassword forgotPassword = forgotPasswordRepository.findByOtpAndUser(Integer.valueOf(otp), userExists)
                    .orElseThrow(() -> new HandlerException(ErrorCodeEnum.ER110.getCode(), ErrorCodeEnum.ER110.getMessage(), HttpStatus.BAD_REQUEST));

            if (forgotPassword.getExpirationTime().before(Date.from(Instant.now()))) {
                throw new HandlerException(ErrorCodeEnum.ER111.getCode(), ErrorCodeEnum.ER111.getMessage(), HttpStatus.BAD_REQUEST);
            }

            String temporaryToken = jwtUtil.generateAccessToken(userExists.getEmail(), temporaryTokenExp);
            result.setAccessToken(temporaryToken);
            result.setMessage(message);

            return result;
        } catch (HandlerException handlerException) {
            handlerException.printStackTrace();
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), PathEnum.VERIFY_EMAIL_PATH.getPath(), handlerException.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.VERIFY_EMAIL_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public String changePassword(HttpServletRequest request, ChangePasswordRequest passwordRequest) {
        String message = "Password has been changed!";
        try {
            if (!Objects.equals(passwordRequest.getPassword(), passwordRequest.getRepeatPassword())) {
                throw new HandlerException(ErrorCodeEnum.ER112.getCode(), ErrorCodeEnum.ER112.getMessage(), HttpStatus.BAD_REQUEST);
            }

            User userExists = jwtUtil.extraUserFromRequest(request);

            String encodedPassword = passwordEncoder.encode(passwordRequest.getPassword());
            userRepository.updatePassword(userExists.getEmail(), encodedPassword);

            return message;
        } catch (HandlerException handlerException) {
            handlerException.printStackTrace();
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), PathEnum.VERIFY_EMAIL_PATH.getPath(), handlerException.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.VERIFY_EMAIL_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Integer otpGenerator() {
        Random random = new Random();
        return random.nextInt(100_000, 999_999);
    }
}
