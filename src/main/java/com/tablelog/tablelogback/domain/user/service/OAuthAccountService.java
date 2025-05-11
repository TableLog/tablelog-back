package com.tablelog.tablelogback.domain.user.service;

import com.tablelog.tablelogback.domain.user.dto.service.response.OAuthAccountResponseDto;
import com.tablelog.tablelogback.domain.user.entity.OAuthAccount;
import com.tablelog.tablelogback.domain.user.exception.NotFoundOAuthAccountException;
import com.tablelog.tablelogback.domain.user.exception.UserErrorCode;
import com.tablelog.tablelogback.domain.user.repository.OAuthAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class OAuthAccountService {
    private final OAuthAccountRepository oAuthAccountRepository;

    public List<OAuthAccountResponseDto> getAllOAuthAccountDtos(Long userId){
        List<OAuthAccount> oAuthAccounts = oAuthAccountRepository.findAllByUserId(userId)
                .orElseThrow(() -> new NotFoundOAuthAccountException(UserErrorCode.NOT_FOUND_SOCIAL_ACCOUNT));
        List<OAuthAccountResponseDto> dtos = oAuthAccounts.stream()
                .map(account -> new OAuthAccountResponseDto(account.getProvider(), account.getEmail()))
                .toList();
        return dtos;
    }
}
