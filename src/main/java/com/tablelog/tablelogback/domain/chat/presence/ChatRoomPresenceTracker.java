package com.tablelog.tablelogback.domain.chat.presence;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 채팅방 "입장(구독)" 상태 추적용(메모리).
 * - WS 세션이 특정 room destination을 구독 중이면 해당 유저가 방에 들어와 있다고 간주
 * - 로컬/단일 인스턴스에서 실시간 읽음 처리용으로 사용
 */
@Component
public class ChatRoomPresenceTracker {

    private static final class Key {
        private final String sessionId;
        private final String subscriptionId;

        private Key(String sessionId, String subscriptionId) {
            this.sessionId = sessionId;
            this.subscriptionId = subscriptionId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key key = (Key) o;
            return sessionId.equals(key.sessionId) && subscriptionId.equals(key.subscriptionId);
        }

        @Override
        public int hashCode() {
            int result = sessionId.hashCode();
            result = 31 * result + subscriptionId.hashCode();
            return result;
        }
    }

    private record Entry(String roomId, Long userId) {}

    // (sessionId, subscriptionId) -> (roomId, userId)
    private final Map<Key, Entry> subscriptions = new ConcurrentHashMap<>();

    // roomId -> userIds currently present
    private final Map<String, Set<Long>> roomUsers = new ConcurrentHashMap<>();

    public void onSubscribe(String sessionId, String subscriptionId, String roomId, Long userId) {
        if (sessionId == null || subscriptionId == null || roomId == null || userId == null) return;

        Key key = new Key(sessionId, subscriptionId);
        subscriptions.put(key, new Entry(roomId, userId));

        roomUsers.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(userId);
    }

    public void onUnsubscribe(String sessionId, String subscriptionId) {
        if (sessionId == null || subscriptionId == null) return;
        Key key = new Key(sessionId, subscriptionId);
        Entry removed = subscriptions.remove(key);
        if (removed == null) return;

        Set<Long> users = roomUsers.get(removed.roomId());
        if (users != null) {
            users.remove(removed.userId());
            if (users.isEmpty()) {
                roomUsers.remove(removed.roomId());
            }
        }
    }

    public void onDisconnect(String sessionId) {
        if (sessionId == null) return;
        // sessionId에 해당하는 모든 subscription 제거
        subscriptions.keySet().removeIf(key -> {
            if (!key.sessionId.equals(sessionId)) return false;
            onUnsubscribe(key.sessionId, key.subscriptionId);
            return true;
        });
    }

    public boolean isUserInRoom(String roomId, Long userId) {
        if (roomId == null || userId == null) return false;
        Set<Long> users = roomUsers.get(roomId);
        return users != null && users.contains(userId);
    }
}

