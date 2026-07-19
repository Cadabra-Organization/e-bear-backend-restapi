package com.example.ebearrestapi.filter;

import com.example.ebearrestapi.dto.request.UserDto;
import com.example.ebearrestapi.etc.Role;
import com.example.ebearrestapi.utils.JwtProperties;
import com.example.ebearrestapi.utils.JwtToken;
import com.example.ebearrestapi.vo.UserDetail;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private static final Set<String> ADMIN_PAGE_ORIGINS = Set.of(
            "http://localhost:5174",
            "http://127.0.0.1:5174"
    );

    private  final JwtToken jwtToken;
    private  final AuthenticationManager authenticationManager;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try{
            ObjectMapper om = new ObjectMapper();
            UserDto user = om.readValue(request.getInputStream(), UserDto.class);

            String username = user.getUserId();
            String password = user.getPassword();

            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username,password);

            return authenticationManager.authenticate(authRequest);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        UserDetail principalUserDetails = (UserDetail)authResult.getPrincipal();

        String access = jwtToken.createToken(authResult);
        if (isAdminPageOrigin(request) && principalUserDetails.getRole() != Role.ADMIN) {
            setFailResponse(response, "관리자 페이지는 관리자 계정만 접속 가능합니다.");
            return;
        }

        response.addHeader(JwtProperties.HEADER_ACCESS_STRING, JwtProperties.TOKEN_PREFIX + access);
        setSuccessResponse(response, "로그인 성공");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {

        String failReason = failed.getMessage();
        setFailResponse(response, failReason);
    }

    private void setSuccessResponse(HttpServletResponse response, String message) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8"); // 한글 깨짐 방지

        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("code", 1);
        body.put("message", message);

        response.getWriter().print(objectMapper.writeValueAsString(body));
    }

    private void setFailResponse(HttpServletResponse response, String message) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("code", -1);
        body.put("message", message);

        response.getWriter().print(objectMapper.writeValueAsString(body));
    }

    private boolean isAdminPageOrigin(HttpServletRequest request) {
        String origin = request.getHeader("Origin");
        if (origin != null) {
            return ADMIN_PAGE_ORIGINS.contains(origin);
        }

        String referer = request.getHeader("Referer");
        return referer != null && ADMIN_PAGE_ORIGINS.stream().anyMatch(referer::startsWith);
    }
}
