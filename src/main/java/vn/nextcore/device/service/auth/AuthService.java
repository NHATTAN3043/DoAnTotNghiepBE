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
import vn.nextcore.device.dto.req.AuthRequest;
import vn.nextcore.device.dto.req.TokenRefreshRequest;
import vn.nextcore.device.dto.resp.AuthResponse;
import vn.nextcore.device.dto.resp.ReTokenResponse;
import vn.nextcore.device.entity.User;
import vn.nextcore.device.enums.ErrorCodeEnum;
import vn.nextcore.device.enums.PathEnum;
import vn.nextcore.device.exception.HandlerException;
import vn.nextcore.device.repository.UserRepository;
import vn.nextcore.device.security.jwt.JwtUtil;
import vn.nextcore.device.util.TokenBlackListUtil;

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

    private final TokenBlackListUtil tokenBlackUtil;

    public AuthService(TokenBlackListUtil tokenBlackUtil) {
        this.tokenBlackUtil = tokenBlackUtil;
    }

    public AuthResponse loginWithEmailAndPassword(AuthRequest authRequest) {
        AuthResponse authResponse = new AuthResponse();
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String accessToken = jwtUtil.generateAccessToken(authRequest.getEmail());
            authResponse.setAccessToken(accessToken);

            if (userRepository.existsByEmail(authRequest.getEmail())) {
                User userExists = userRepository.findByEmail(authRequest.getEmail());
                String refreshToken = jwtUtil.generateRefreshToken(String.valueOf(userExists.getId()));
                authResponse.setRefreshToken(refreshToken);
                authResponse.setRoleId(userExists.getRole().getId());
            }
            authResponse.setExpireIn(jwtExpirationMs);
            return authResponse;
        } catch (BadCredentialsException e) {
            throw new HandlerException(ErrorCodeEnum.ER007.getCode(), ErrorCodeEnum.ER007.getMessage(), PathEnum.LOGIN_PATH.getPath(), HttpStatus.UNAUTHORIZED);
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

        if (tokenBlackUtil.isTokenBlacklisted(request.getRefreshToken())) {
            throw new HandlerException(ErrorCodeEnum.ER100.getCode(), ErrorCodeEnum.ER100.getMessage(), PathEnum.REFRESH_TOKEN_PATH.getPath(), HttpStatus.UNAUTHORIZED);
        }
        String newAccessToken = jwtUtil.refreshAccessToken(request.getRefreshToken());
        reTokenResponse.setAccessToken(newAccessToken);
        reTokenResponse.setExpireIn(jwtExpirationMs);
        return reTokenResponse;
    }

    @Override
    public void logout(HttpServletRequest request, TokenRefreshRequest refreshToken) {
        try {
            String accessToken = jwtUtil.extraJwtTokenFromRequest(request);
            long accessTokenExpiry = jwtUtil.extractExpiration(accessToken).getTime() - System.currentTimeMillis();
            long refreshTokenExpiry = jwtUtil.extractExpiration(refreshToken.getRefreshToken()).getTime() - System.currentTimeMillis();

            // add token access and refresh in black list
            tokenBlackUtil.addBlacklistToken(accessToken, accessTokenExpiry);
            tokenBlackUtil.addBlacklistToken(refreshToken.getRefreshToken(), refreshTokenExpiry);
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.LOGIN_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
