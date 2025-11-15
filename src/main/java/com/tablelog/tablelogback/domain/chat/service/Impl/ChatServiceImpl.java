package com.tablelog.tablelogback.domain.chat.service.Impl;

import com.tablelog.tablelogback.domain.chat.dto.service.ChatMessageServiceRequestDto;
import com.tablelog.tablelogback.domain.chat.dto.service.ChatMessageServiceResponseDto;
import com.tablelog.tablelogback.domain.chat.entity.Chat;
import com.tablelog.tablelogback.domain.chat.dto.service.ChatRoomLastMessageResponseDto;
import com.tablelog.tablelogback.domain.chat.exception.ChatErrorCode;
import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.domain.chat.exception.NotFoundChatException;
import com.tablelog.tablelogback.domain.chat.mapper.entity.ChatEntityMapper;
import com.tablelog.tablelogback.domain.chat.repository.ChatRepository;
import com.tablelog.tablelogback.domain.chat.service.ChatService;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.domain.user.repository.UserRepository;
import com.tablelog.tablelogback.global.jwt.JwtUtil;
import com.tablelog.tablelogback.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatServiceImpl implements ChatService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatServiceImpl.class);
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final ChatEntityMapper chatEntityMapper;

    // 세션 접속 시 룸 id 자동으로 생성
    @Override
    public String createChatRoomId(String email) {
        String roomId = email + "-" + UUID.randomUUID().toString();
        LOGGER.info("🏠 채팅방 생성: {} (사용자: {})", roomId, email);
        return roomId;
    }

    // 채팅 메시지 저장
    @Override
    public ChatMessageServiceResponseDto saveChatMessage(ChatMessageServiceRequestDto chatMessageServiceRequestDto) {
        try {
            // DTO -> Entity 변환
            Chat chat = chatEntityMapper.toChat(chatMessageServiceRequestDto);
            // sender/receiver/deliveredAt 설정
            String senderEmail = chatMessageServiceRequestDto.senderEmail();
            chat.setSenderEmail(senderEmail);
            // roomId에서 상대 userId 결정 (userIdA--userIdB 규칙)
            String roomId = chatMessageServiceRequestDto.roomId();
            User receiverUser = null;
            String[] parts = roomId != null ? roomId.split("--", 2) : new String[0];
            if (parts.length == 2) {
                try {
                    Long part0 = Long.parseLong(parts[0]);
                    Long part1 = Long.parseLong(parts[1]);
                    // senderEmail로 User를 찾아서 sender의 userId 확인
                    User senderUser = userRepository.findByEmail(senderEmail)
                        .orElseThrow(() -> new RuntimeException("발신자 사용자를 찾을 수 없습니다: " + senderEmail));
                    Long senderUserId = senderUser.getId();
                    
                    // 상대방 userId 결정
                    Long receiverUserId = senderUserId.equals(part0) ? part1 : part0;
                    
                    // 상대방 User 조회하여 receiverEmail 설정
                    receiverUser = userRepository.findById(receiverUserId)
                        .orElseThrow(() -> new RuntimeException("수신자 사용자를 찾을 수 없습니다: " + receiverUserId));
                    chat.setReceiverEmail(receiverUser.getEmail());
                } catch (NumberFormatException e) {
                    LOGGER.warn("⚠️ roomId 형식 오류 (userId 기반이 아님): {}", roomId);
                    // 기존 로직으로 폴백 (email 기반)
                    String p0 = parts[0] != null ? parts[0].trim().toLowerCase() : "";
                    String p1 = parts[1] != null ? parts[1].trim().toLowerCase() : "";
                    String s = senderEmail != null ? senderEmail.trim().toLowerCase() : "";
                    if (s.equals(p0)) {
                        chat.setReceiverEmail(parts[1]);
                    } else if (s.equals(p1)) {
                        chat.setReceiverEmail(parts[0]);
                    } else {
                        chat.setReceiverEmail(parts[1]);
                    }
                }
            }
            chat.setDeliveredAt(java.time.LocalDateTime.now());
            
            // 데이터베이스에 채팅 메시지 저장
            Chat savedChat = chatRepository.save(chat);

            LOGGER.info("💾 채팅 메시지 저장: {} (룸: {}, ID: {})",
                savedChat.getMessage(), savedChat.getRoomId(), savedChat.getId());

            // Entity -> DTO 변환하여 반환 (수신자 정보 포함)
            return chatEntityMapper.toChatMessageServiceResponseDto(
                savedChat,
                receiverUser != null ? receiverUser.getNickname() : null,
                receiverUser != null ? receiverUser.getProfileImgUrl() : null
            );

        } catch (Exception e) {
            LOGGER.error("❌ 채팅 메시지 저장 실패: ", e);
            throw new RuntimeException("채팅 메시지 저장에 실패했습니다.", e);
        }
    }

    // 특정 채팅방의 메시지 목록 조회 (최신순)
    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageServiceResponseDto> getChatMessages(String roomId) {
        try {
            String reverseRoomId = getReverseRoomId(roomId);
            List<Chat> chatList = chatRepository.findByRoomIdOrderByCreatedAtDesc(roomId, reverseRoomId);
            LOGGER.info("📋 채팅방 {} 메시지 조회: {}개", roomId, chatList.size());
            
            // 상대방 정보 조회 (첫 번째 메시지에서 senderEmail을 통해)
            final User receiver;
            if (!chatList.isEmpty()) {
                String senderEmail = chatList.get(0).getSenderEmail();
                User senderUser = userRepository.findByEmail(senderEmail).orElse(null);
                if (senderUser != null) {
                    receiver = getReceiverFromRoomId(roomId, senderUser.getId());
                } else {
                    receiver = null;
                }
            } else {
                receiver = null;
            }
            
            // Entity -> DTO 변환하여 반환
            return chatList.stream()
                .map(chat -> chatEntityMapper.toChatMessageServiceResponseDto(
                    chat,
                    receiver != null ? receiver.getNickname() : null,
                    receiver != null ? receiver.getProfileImgUrl() : null
                ))
                .collect(Collectors.toList());

        } catch (Exception e) {
            LOGGER.error("❌ 채팅 메시지 조회 실패: ", e);
            throw new RuntimeException("채팅 메시지 조회에 실패했습니다.", e);
        }
    }

    // 특정 채팅방의 메시지 목록 조회 (오래된순)
    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageServiceResponseDto> getChatMessagesAsc(String roomId) {
        try {
            String reverseRoomId = getReverseRoomId(roomId);
            List<Chat> chatList = chatRepository.findByRoomIdOrderByCreatedAtAsc(roomId, reverseRoomId);
            LOGGER.info("📋 채팅방 {} 메시지 조회 (오래된순): {}개", roomId, chatList.size());
            
            // 상대방 정보 조회 (첫 번째 메시지에서 senderEmail을 통해)
            final User receiver;
            if (!chatList.isEmpty()) {
                String senderEmail = chatList.get(0).getSenderEmail();
                User senderUser = userRepository.findByEmail(senderEmail).orElse(null);
                if (senderUser != null) {
                    receiver = getReceiverFromRoomId(roomId, senderUser.getId());
                } else {
                    receiver = null;
                }
            } else {
                receiver = null;
            }
            
            // Entity -> DTO 변환하여 반환
            return chatList.stream()
                .map(chat -> chatEntityMapper.toChatMessageServiceResponseDto(
                    chat,
                    receiver != null ? receiver.getNickname() : null,
                    receiver != null ? receiver.getProfileImgUrl() : null
                ))
                .collect(Collectors.toList());

        } catch (Exception e) {
            LOGGER.error("❌ 채팅 메시지 조회 실패: ", e);
            throw new RuntimeException("채팅 메시지 조회에 실패했습니다.", e);
        }
    }

    // 특정 채팅방의 메시지 개수 조회
    @Override
    @Transactional(readOnly = true)
    public long getChatMessageCount(String roomId) {
        try {
            long count = chatRepository.countByRoomId(roomId);
            LOGGER.info("📊 채팅방 {} 메시지 개수: {}개", roomId, count);
            return count;

        } catch (Exception e) {
            LOGGER.error("❌ 채팅 메시지 개수 조회 실패: ", e);
            throw new RuntimeException("채팅 메시지 개수 조회에 실패했습니다.", e);
        }
    }

    // 특정 사용자의 채팅 메시지 조회
    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageServiceResponseDto> getChatMessagesByUser(String sender) {
        try {
            List<Chat> chatList = chatRepository.findBySenderOrderByCreatedAtDesc(sender);
            LOGGER.info("👤 발신자 {} 메시지 조회: {}개", sender, chatList.size());
            
            // sender로 User를 찾아서 receiver 조회
            User senderUser = userRepository.findByNickname(sender).orElse(null);
            
            return chatList.stream()
                .map(chat -> {
                    User receiver = null;
                    if (senderUser != null) {
                        receiver = getReceiverFromRoomId(chat.getRoomId(), senderUser.getId());
                    }
                    return chatEntityMapper.toChatMessageServiceResponseDto(
                        chat,
                        receiver != null ? receiver.getNickname() : null,
                        receiver != null ? receiver.getProfileImgUrl() : null
                    );
                })
                .collect(Collectors.toList());

        } catch (Exception e) {
            LOGGER.error("❌ 발신자 채팅 메시지 조회 실패: ", e);
            throw new RuntimeException("발신자 채팅 메시지 조회에 실패했습니다.", e);
        }
    }

    // 모든 채팅 메시지 전체 조회 (최신순)
    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageServiceResponseDto> getAllChatMessages() {
        try {
            List<Chat> chatList = chatRepository.findAllByOrderByCreatedAtDesc();
            LOGGER.info("🗂️ 전체 채팅 메시지 조회: {}개", chatList.size());

            // 각 메시지의 roomId를 사용하여 수신자 정보 조회
            return chatList.stream()
                .map(chat -> {
                    User receiver = null;
                    // senderEmail로 sender User를 찾아서 receiver 조회
                    try {
                        User senderUser = userRepository.findByEmail(chat.getSenderEmail()).orElse(null);
                        if (senderUser != null) {
                            receiver = getReceiverFromRoomId(chat.getRoomId(), senderUser.getId());
                        }
                    } catch (Exception e) {
                        LOGGER.warn("⚠️ 수신자 정보 조회 실패: roomId={}, senderEmail={}", 
                            chat.getRoomId(), chat.getSenderEmail());
                    }
                    return chatEntityMapper.toChatMessageServiceResponseDto(
                        chat,
                        receiver != null ? receiver.getNickname() : null,
                        receiver != null ? receiver.getProfileImgUrl() : null
                    );
                })
                .collect(Collectors.toList());

        } catch (Exception e) {
            LOGGER.error("❌ 전체 채팅 메시지 조회 실패: ", e);
            throw new RuntimeException("전체 채팅 메시지 조회에 실패했습니다.", e);
        }
    }

    // 특정 채팅방의 메시지 목록 조회 (최신순) - 권한 검증 포함
    @Override
    @Transactional
    public List<ChatMessageServiceResponseDto> getChatMessagesWithAuth(String roomId, User currentUser) {
        // 2인 룸 규칙: roomId 참가자 여부로 권한 검증
        Long userId = currentUser.getId();
        if (!isParticipant(roomId, userId)) {
            LOGGER.warn("🚫 권한 없음: 사용자 ID {}가 채팅방 {}에 접근 시도", userId, roomId);
            throw new CustomException(ChatErrorCode.UNAUTHORIZED_CHAT);
        }

        LOGGER.info("✅ 권한 확인 완료: 사용자 ID {}가 채팅방 {} 조회", userId, roomId);

        // 상대방 정보 조회
        User receiver = getReceiverFromRoomId(roomId, userId);
        
        // 권한 확인 후 조회 (정규화된 roomId와 역순 roomId 모두 검색)
        String reverseRoomId = getReverseRoomId(roomId);
        List<Chat> chatList = chatRepository.findByRoomIdOrderByCreatedAtDesc(roomId, reverseRoomId);
        List<ChatMessageServiceResponseDto> result = chatList.stream()
            .map(chat -> chatEntityMapper.toChatMessageServiceResponseDto(
                chat,
                receiver != null ? receiver.getNickname() : null,
                receiver != null ? receiver.getProfileImgUrl() : null
            ))
            .collect(Collectors.toList());
        
        // 조회 시 읽음 처리 (서비스 계층)
        markRoomRead(roomId, currentUser);
        return result;
    }

    // 특정 채팅방의 메시지 목록 조회 (오래된순) - 권한 검증 포함
    @Override
    @Transactional
    public List<ChatMessageServiceResponseDto> getChatMessagesAscWithAuth(String roomId, User currentUser) {
        // 2인 룸 규칙: roomId 참가자 여부로 권한 검증
        Long userId = currentUser.getId();
        if (!isParticipant(roomId, userId)) {
            LOGGER.warn("🚫 권한 없음: 사용자 ID {}가 채팅방 {}에 접근 시도", userId, roomId);
            throw new CustomException(ChatErrorCode.UNAUTHORIZED_CHAT);
        }

        LOGGER.info("✅ 권한 확인 완료: 사용자 ID {}가 채팅방 {} 조회 (오래된순)", userId, roomId);

        // 상대방 정보 조회
        User receiver = getReceiverFromRoomId(roomId, userId);
        
        // 권한 확인 후 조회 (정규화된 roomId와 역순 roomId 모두 검색)
        String reverseRoomId = getReverseRoomId(roomId);
        List<Chat> chatList = chatRepository.findByRoomIdOrderByCreatedAtAsc(roomId, reverseRoomId);
        List<ChatMessageServiceResponseDto> result = chatList.stream()
            .map(chat -> chatEntityMapper.toChatMessageServiceResponseDto(
                chat,
                receiver != null ? receiver.getNickname() : null,
                receiver != null ? receiver.getProfileImgUrl() : null
            ))
            .collect(Collectors.toList());
        
        // 조회 시 읽음 처리 (서비스 계층)
        markRoomRead(roomId, currentUser);
        return result;
    }

    /**
     * SecurityContext에서 현재 인증된 사용자 정보 가져오기 (HTTP 요청용)
     * @return User 객체 또는 null
     */
    @Override
    public User getCurrentAuthenticatedUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                return userDetails.user();
            }
        } catch (Exception e) {
            LOGGER.error("❌ 사용자 정보 가져오기 실패: ", e);
        }
        return null;
    }

    /**
     * STOMP 헤더에서 현재 인증된 사용자 정보 가져오기
     * @param accessor STOMP 헤더 접근자
     * @return User 객체 또는 null
     */
    @Override
    public User getCurrentUser(StompHeaderAccessor accessor) {
        try {
            LOGGER.info("🔍 Starting user authentication process...");
            
            // 방법 1: 세션 속성에서 UserDetailsImpl 가져오기 (CustomHandshakeInterceptor에서 설정)
            Object userDetailsObj = accessor.getSessionAttributes().get("userDetails");
            LOGGER.info("🔍 Session userDetails: {}", userDetailsObj != null ? userDetailsObj.getClass().getSimpleName() : "null");
            if (userDetailsObj instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsObj;
                LOGGER.info("✅ User found from session (UserDetailsImpl): {}", userDetails.user().getNickname());
                return userDetails.user();
            }
            
            // 방법 2: SecurityContext에서 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            LOGGER.info("🔐 SecurityContext authentication: {}", authentication != null ? authentication.getName() : "null");
            if (authentication != null && authentication.isAuthenticated() 
                && !"anonymousUser".equals(authentication.getPrincipal())) {
                
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                LOGGER.info("✅ User found from SecurityContext: {}", userDetails.user().getNickname());
                return userDetails.user();
            }
            
            // 방법 3: JWT 토큰에서 사용자 정보 추출 (fallback)
            LOGGER.info("🔍 Attempting JWT token extraction as fallback...");
            String accessToken = getAccessTokenFromSession(accessor);
            if (accessToken != null) {
                LOGGER.info("🔑 Access token found: {}", accessToken.substring(0, Math.min(20, accessToken.length())) + "...");
                try {
                    // JWT 토큰 검증 및 사용자 정보 추출
                    if (jwtUtil.validateToken(accessToken)) {
                        String email = jwtUtil.getUserInfoFromToken(accessToken).getSubject();
                        LOGGER.info("📧 Email from JWT: {}", email);
                        User user = userRepository.findByEmail(email)
                            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + email));
                        LOGGER.info("👤 User found from JWT: {} (ID: {})", user.getNickname(), user.getId());
                        return user;
                    } else {
                        LOGGER.warn("⚠️ Invalid JWT token");
                    }
                } catch (io.jsonwebtoken.security.SignatureException e) {
                    LOGGER.warn("⚠️ JWT signature verification failed - token may be expired or invalid");
                    LOGGER.warn("🔍 Token preview: {}", accessToken.substring(0, Math.min(50, accessToken.length())) + "...");
                    
                    // 임시로 JWT 검증 없이 토큰에서 이메일 추출 시도
                    try {
                        String email = jwtUtil.getUserInfoFromToken(accessToken).getSubject();
                        LOGGER.info("📧 Email extracted from JWT (without validation): {}", email);
                        User user = userRepository.findByEmail(email)
                            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + email));
                        LOGGER.info("👤 User found from JWT (unvalidated): {} (ID: {})", user.getNickname(), user.getId());
                        return user;
                    } catch (Exception ex) {
                        LOGGER.warn("⚠️ Failed to extract email from JWT token");
                    }
                } catch (io.jsonwebtoken.ExpiredJwtException e) {
                    LOGGER.warn("⚠️ JWT token has expired");
                } catch (Exception e) {
                    LOGGER.error("❌ Error parsing JWT token: ", e);
                }
            } else {
                LOGGER.warn("⚠️ No access token found");
            }
            
        } catch (Exception e) {
            LOGGER.error("❌ Error getting current user: ", e);
        }
        
        LOGGER.warn("⚠️ No authenticated user found after all methods");
        return null;
    }

    /**
     * STOMP 세션에서 accessToken 추출
     * @param accessor STOMP 헤더 접근자
     * @return accessToken 또는 null
     */
    private String getAccessTokenFromSession(StompHeaderAccessor accessor) {
        try {
            LOGGER.info("🔍 Searching for access token...");
            
            // 세션 속성에서 accessToken 가져오기
            Object accessToken = accessor.getSessionAttributes().get("accessToken");
            LOGGER.info("🔍 Session accessToken: {}", accessToken != null ? "found" : "null");
            if (accessToken != null) {
                LOGGER.info("✅ Access token found in session");
                return accessToken.toString();
            }
            
            // STOMP 헤더에서 쿠키 정보 가져오기
            String cookieHeader = accessor.getFirstNativeHeader("Cookie");
            LOGGER.info("🔍 Cookie header: {}", cookieHeader != null ? cookieHeader : "null");
            if (cookieHeader != null) {
                LOGGER.info("🍪 Cookie header found: {}", cookieHeader);
                String[] cookies = cookieHeader.split(";");
                for (String cookie : cookies) {
                    String[] parts = cookie.trim().split("=");
                    if (parts.length == 2 && "accessToken".equals(parts[0])) {
                        LOGGER.info("✅ Access token found in cookie");
                        return parts[1];
                    }
                }
            }
            
            // Authorization 헤더에서 Bearer 토큰 가져오기
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            LOGGER.info("🔍 Authorization header: {}", authHeader != null ? authHeader : "null");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                LOGGER.info("🔐 Authorization header found");
                return authHeader.substring(7); // "Bearer " 제거
            }
            
            // accessToken 헤더에서 직접 가져오기
            String accessTokenHeader = accessor.getFirstNativeHeader("accessToken");
            LOGGER.info("🔍 accessToken header: {}", accessTokenHeader != null ? "found" : "null");
            if (accessTokenHeader != null) {
                LOGGER.info("✅ Access token found in accessToken header");
                return accessTokenHeader;
            }
            
            // 커스텀 헤더에서 토큰 가져오기
            String customToken = accessor.getFirstNativeHeader("X-Access-Token");
            LOGGER.info("🔍 X-Access-Token header: {}", customToken != null ? customToken : "null");
            if (customToken != null) {
                LOGGER.info("🎫 Custom token header found");
                return customToken;
            }
            
            LOGGER.warn("⚠️ No access token found in any location");
            
        } catch (Exception e) {
            LOGGER.error("❌ Error extracting access token: ", e);
        }
        
        return null;
    }

    /**
     * 채팅방 구독 권한 검증
     * @param roomId 채팅방 ID
     * @param accessor STOMP 헤더 접근자
     * @throws IllegalArgumentException 인증 실패 또는 권한 없음 시
     */
    @Override
    public void validateChatRoomSubscription(String roomId, StompHeaderAccessor accessor) {
        LOGGER.info("🔍 채팅방 구독 권한 검증 시작: roomId={}", roomId);
        
        // 1. 현재 사용자 가져오기
        User currentUser = getCurrentUser(accessor);
        if (currentUser == null) {
            LOGGER.warn("🚫 구독 거부: 인증되지 않은 사용자의 채팅방 구독 시도 - roomId={}", roomId);
            throw new IllegalArgumentException("인증이 필요합니다.");
        }
        
        Long userId = currentUser.getId();
        LOGGER.info("👤 구독 시도 사용자: {} (ID: {}, Email: {})", currentUser.getNickname(), userId, currentUser.getEmail());
        
        // 2. 권한 검증: 2인 룸 규칙 참가자 여부 확인
        if (!isParticipant(roomId, userId)) {
            LOGGER.warn("🚫 구독 거부: 권한 없음 - 사용자 ID {}가 채팅방 {} 구독 시도", userId, roomId);
            throw new IllegalArgumentException("해당 채팅방에 접근할 수 있는 권한이 없습니다.");
        }
        
        LOGGER.info("✅ 구독 권한 검증 완료: 사용자 ID {}가 채팅방 {} 구독 허용", userId, roomId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatRoomLastMessageResponseDto> getOwnedChatRooms(User currentUser) {
        if (currentUser == null) {
            throw new CustomException(ChatErrorCode.UNAUTHORIZED_CHAT);
        }
        Long userId = currentUser.getId();
        List<ChatRepository.LastMessageProjection> rows = chatRepository.findLastMessagesForParticipant(userId);
        return rows.stream()
            .map(r -> {
                long unreadCount = chatRepository.countByRoomIdAndReceiverEmailAndReadAtIsNull(r.getRoomId(), currentUser.getEmail());
                User receiver = getReceiverFromRoomId(r.getRoomId(), userId);
                return chatEntityMapper.toChatRoomLastMessageResponseDto(
                    r,
                    unreadCount,
                    receiver != null ? receiver.getNickname() : null,
                    receiver != null ? receiver.getProfileImgUrl() : null
                );
            })
            .collect(Collectors.toList());
    }

    @Override
    public String buildPairRoomId(Long userIdA, Long userIdB) {
        if (userIdA == null || userIdB == null) {
            throw new IllegalArgumentException("사용자 ID는 null일 수 없습니다.");
        }
        if (userIdA <= userIdB) {
            return userIdA + "--" + userIdB;
        }
        return userIdB + "--" + userIdA;
    }

    @Override
    public boolean isParticipant(String roomId, Long userId) {
        if (roomId == null || userId == null) {
            return false;
        }
        String[] parts = roomId.split("--", 2);
        if (parts.length != 2) {
            return false;
        }
        try {
            Long part0 = Long.parseLong(parts[0]);
            Long part1 = Long.parseLong(parts[1]);
            return userId.equals(part0) || userId.equals(part1);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(String roomId, User currentUser) {
        if (currentUser == null || !isParticipant(roomId, currentUser.getId())) {
            throw new CustomException(ChatErrorCode.UNAUTHORIZED_CHAT);
        }
        return chatRepository.countByRoomIdAndReceiverEmailAndReadAtIsNull(roomId, currentUser.getEmail());
    }

    @Override
    public int markRoomRead(String roomId, User currentUser) {
        if (currentUser == null || !isParticipant(roomId, currentUser.getId())) {
            throw new CustomException(ChatErrorCode.UNAUTHORIZED_CHAT);
        }
        return chatRepository.markRoomRead(roomId, currentUser.getEmail());
    }

    /**
     * roomId와 현재 사용자 ID를 통해 상대방 User 정보 조회
     * @param roomId 채팅방 ID (userIdA--userIdB 형식)
     * @param currentUserId 현재 사용자 ID
     * @return 상대방 User 객체 또는 null
     */
    private User getReceiverFromRoomId(String roomId, Long currentUserId) {
        if (roomId == null || currentUserId == null) {
            return null;
        }
        String[] parts = roomId.split("--", 2);
        if (parts.length != 2) {
            return null;
        }
        try {
            Long part0 = Long.parseLong(parts[0]);
            Long part1 = Long.parseLong(parts[1]);
            Long receiverUserId = currentUserId.equals(part0) ? part1 : part0;
            return userRepository.findById(receiverUserId).orElse(null);
        } catch (NumberFormatException e) {
            LOGGER.warn("⚠️ roomId 형식 오류: {}", roomId);
            return null;
        }
    }

    /**
     * roomId의 역순을 반환 (10--6 -> 6--10)
     * @param roomId 채팅방 ID (userIdA--userIdB 형식)
     * @return 역순 roomId 또는 원본 roomId (파싱 실패 시)
     */
    private String getReverseRoomId(String roomId) {
        if (roomId == null || roomId.isEmpty()) {
            return roomId;
        }
        String[] parts = roomId.split("--", 2);
        if (parts.length != 2) {
            return roomId;
        }
        try {
            Long part0 = Long.parseLong(parts[0].trim());
            Long part1 = Long.parseLong(parts[1].trim());
            // 역순 반환
            return part1 + "--" + part0;
        } catch (NumberFormatException e) {
            LOGGER.warn("⚠️ roomId 파싱 실패: {}", roomId);
            return roomId;
        }
    }
}