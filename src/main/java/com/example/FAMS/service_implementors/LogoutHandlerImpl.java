package com.example.FAMS.service_implementors;

import com.example.FAMS.repositories.TokenDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutHandlerImpl implements LogoutHandler {

    private final TokenDAO tokenDAO;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        String authenHeader = request.getHeader("Authorization");
        String token;
        if (authenHeader == null || !authenHeader.startsWith("Bearer ")) {
            return;
        }
        token = authenHeader.substring(7);
        var storedToken = tokenDAO.findByToken(token).orElse(null);
        if (storedToken != null) {
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenDAO.save(storedToken);
        }
    }
}
