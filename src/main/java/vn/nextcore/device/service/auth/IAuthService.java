package vn.nextcore.device.service.auth;

import jakarta.servlet.http.HttpServletRequest;
import vn.nextcore.device.dto.req.AuthRequest;
import vn.nextcore.device.dto.req.TokenRefreshRequest;
import vn.nextcore.device.dto.resp.AuthResponse;
import vn.nextcore.device.dto.resp.DataResponse;
import vn.nextcore.device.dto.resp.ReTokenResponse;

public interface IAuthService {
    AuthResponse loginWithEmailAndPassword(AuthRequest authRequest);

    ReTokenResponse refreshToken(TokenRefreshRequest request);

    public void logout(HttpServletRequest request, TokenRefreshRequest refreshToken);
}
