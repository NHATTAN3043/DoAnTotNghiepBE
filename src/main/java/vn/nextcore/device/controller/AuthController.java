package vn.nextcore.device.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.nextcore.device.dto.req.AuthRequest;
import vn.nextcore.device.dto.req.TokenRefreshRequest;
import vn.nextcore.device.dto.resp.*;
import vn.nextcore.device.service.auth.IAuthService;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {
    @Autowired
    private IAuthService authService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public DataResponse<AuthResponse> login(@Validated @RequestBody AuthRequest authRequest) {
        AuthResponse authResponse = authService.loginWithEmailAndPassword(authRequest);
        return new DataResponse<>(authResponse);
    }

    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ReTokenResponse.class))),
        @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/refreshtoken")
    @ResponseStatus(HttpStatus.OK)
    public DataResponse<ReTokenResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        ReTokenResponse reTokenResponse = authService.refreshToken(request);
        return new DataResponse<>(reTokenResponse);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ReTokenResponse.class))),
            @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public DataResponse<String> logout(
            HttpServletRequest request,
            @Valid @RequestBody TokenRefreshRequest refreshToken) {
        authService.logout(request, refreshToken);
        return new DataResponse<>("Logout successful");
    }
}
