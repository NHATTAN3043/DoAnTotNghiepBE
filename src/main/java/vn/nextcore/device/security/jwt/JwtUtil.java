package vn.nextcore.device.security.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.nextcore.device.entity.User;
import vn.nextcore.device.enums.ErrorCodeEnum;
import vn.nextcore.device.enums.PathEnum;
import vn.nextcore.device.exception.HandlerException;
import vn.nextcore.device.repository.UserRepository;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JwtUtil {
    @Value("${bezkoder.app.jwtSecret}")
    private String jwtSecret;

    @Value("${bezkoder.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Value("${bezkoder.app.jwtRefreshExpirationMs}")
    private int jwtRefreshExpirationMs;

    private final String AUTHENTICATED_KEY = "Authorization";

    private final String TOKEN_PREFIX = "Bearer ";

    @Autowired
    private UserRepository userRepository;

    public String generateAccessToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    public String generateAccessToken(String email, int jwtExp) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExp))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    public String generateRefreshToken(String id) {
        return Jwts.builder()
                .setSubject(id)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtRefreshExpirationMs))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    public String refreshAccessToken(String refreshToken) {
        try {
            String id = extractUserId(refreshToken);
            if (isTokenExpired(refreshToken) || id == null) {
                throw new HandlerException(ErrorCodeEnum.ER100.getCode(), ErrorCodeEnum.ER100.getMessage(), PathEnum.REFRESH_TOKEN_PATH.getPath(), HttpStatus.UNAUTHORIZED);
            }

            if (userRepository.existsById(Long.valueOf(id))) {
                User user = userRepository.findUserByIdAndDeletedAtIsNull(Long.valueOf(id));
                return generateAccessToken(user.getEmail(), user.getRole().getName());
            } else {
                throw new HandlerException(ErrorCodeEnum.ER100.getCode(), ErrorCodeEnum.ER100.getMessage(), PathEnum.REFRESH_TOKEN_PATH.getPath(), HttpStatus.UNAUTHORIZED);
            }
        } catch (JwtException | IllegalArgumentException | HandlerException e) {
            e.printStackTrace();
            throw new HandlerException(ErrorCodeEnum.ER100.getCode(), ErrorCodeEnum.ER100.getMessage(), PathEnum.REFRESH_TOKEN_PATH.getPath(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.REFRESH_TOKEN_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String extractEmail(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public String extractUserId(String refreshToken) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(refreshToken).getBody().getSubject();
    }

    public boolean isTokenValid(String token, String email) {
        final String extractedEmail = extractEmail(token);
        return (extractedEmail.equals(email) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getExpiration().before(new Date());
    }

    public String extraJwtTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader(AUTHENTICATED_KEY);
        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            return header.substring(7);
        }
        return null;
    }

    public User extraUserFromRequest(HttpServletRequest request) {
        String accessToken = extraJwtTokenFromRequest(request);
        String email = extractEmail(accessToken);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new HandlerException(ErrorCodeEnum.ER025.getCode(), ErrorCodeEnum.ER025.getMessage(), request.getRequestURI(), HttpStatus.BAD_REQUEST);
        }
        return user;
    }

    public Date extractExpiration(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }
}
