package vn.nextcore.device.service.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nextcore.device.dto.req.AuthRequest;
import vn.nextcore.device.dto.req.TokenRefreshRequest;
import vn.nextcore.device.dto.resp.AuthResponse;
import vn.nextcore.device.dto.resp.ReTokenResponse;
import vn.nextcore.device.entity.User;
import vn.nextcore.device.enums.ErrorCodeEnum;
import vn.nextcore.device.enums.PathEnum;
import vn.nextcore.device.exception.HandlerException;
import vn.nextcore.device.repository.DeviceTokensRepository;
import vn.nextcore.device.repository.UserRepository;
import vn.nextcore.device.security.jwt.JwtUtil;

@Service
public class AuthService implements IAuthService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${bezkoder.app.jwtExpirationMs}")
    private Long jwtExpirationMs;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeviceTokensRepository deviceTokensRepository;

    public AuthResponse loginWithEmailAndPassword(AuthRequest authRequest) {
        AuthResponse authResponse = new AuthResponse();
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            if (userRepository.existsByEmailAndDeletedAtIsNull(authRequest.getEmail())) {
                User userExists = userRepository.findByEmail(authRequest.getEmail());
                String refreshToken = jwtUtil.generateRefreshToken(String.valueOf(userExists.getId()));
                authResponse.setRefreshToken(refreshToken);
                authResponse.setRoleId(userExists.getRole().getId());
                authResponse.setUserId(userExists.getId());
                String accessToken = jwtUtil.generateAccessToken(userExists.getEmail(), userExists.getRole().getName());
                authResponse.setAccessToken(accessToken);
            } else {
                throw new HandlerException(ErrorCodeEnum.ER007.getCode(), ErrorCodeEnum.ER007.getMessage(), PathEnum.LOGIN_PATH.getPath(), HttpStatus.BAD_REQUEST);
            }
            authResponse.setExpireIn(jwtExpirationMs);
            return authResponse;
        } catch (BadCredentialsException e) {
            throw new HandlerException(ErrorCodeEnum.ER007.getCode(), ErrorCodeEnum.ER007.getMessage(), PathEnum.LOGIN_PATH.getPath(), HttpStatus.BAD_REQUEST);
        } catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), PathEnum.DEVICE_PATH.getPath(), handlerException.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.LOGIN_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ReTokenResponse refreshToken(TokenRefreshRequest request) {
        ReTokenResponse reTokenResponse = new ReTokenResponse();
        if (request.getRefreshToken() == null) {
            throw new HandlerException(ErrorCodeEnum.ER100.getCode(), ErrorCodeEnum.ER100.getMessage(), PathEnum.REFRESH_TOKEN_PATH.getPath(), HttpStatus.UNAUTHORIZED);
        }

        String newAccessToken = jwtUtil.refreshAccessToken(request.getRefreshToken());
        reTokenResponse.setAccessToken(newAccessToken);
        reTokenResponse.setExpireIn(jwtExpirationMs);
        return reTokenResponse;
    }

    @Override
    @Transactional
    public void logout(HttpServletRequest request, TokenRefreshRequest refreshToken) {
        try {
            User user = jwtUtil.extraUserFromRequest(request);
            deviceTokensRepository.deleteDeviceTokensByUserIdAndPlatform(user.getId(), refreshToken.getPlatform());

        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.LOGIN_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
