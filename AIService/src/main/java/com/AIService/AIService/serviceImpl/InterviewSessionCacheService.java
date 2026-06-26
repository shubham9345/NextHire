package com.AIService.AIService.serviceImpl;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * In-memory session state cache for the interview flow.
 *
 * In-memory implementation. Suitable for a single-instance
 * dev / non-horizontally-scaled deployment. If you ever scale this service out,
 * swap the underlying maps for a distributed store.
 *
 * Tracked state per session:
 *   - current question index
 *   - total questions in session
 *   - answers submitted so far
 *
 * Per-user:
 *   - active session count (used for the concurrent-sessions rate limit)
 */
@Service
@Slf4j
public class InterviewSessionCacheService {

    private final Map<UUID, Integer> totalQuestions = new ConcurrentHashMap<>();
    private final Map<UUID, AtomicInteger> currentIndex = new ConcurrentHashMap<>();
    private final Map<UUID, AtomicInteger> answersCount = new ConcurrentHashMap<>();
    private final Map<UUID, AtomicInteger> userActiveCount = new ConcurrentHashMap<>();

    // ─── Session initialisation ───────────────────────────────────────────────

    public void initSession(UUID sessionId, UUID userId, int total) {
        totalQuestions.put(sessionId, total);
        currentIndex.put(sessionId, new AtomicInteger(0));
        answersCount.put(sessionId, new AtomicInteger(0));
        userActiveCount.computeIfAbsent(userId, k -> new AtomicInteger(0)).incrementAndGet();
        log.info("Session initialised: sessionId={} totalQ={}", sessionId, total);
    }

    // ─── Current question index ───────────────────────────────────────────────

    public int getCurrentIndex(UUID sessionId) {
        AtomicInteger idx = currentIndex.get(sessionId);
        return idx != null ? idx.get() : 0;
    }

    public void incrementCurrentIndex(UUID sessionId) {
        AtomicInteger idx = currentIndex.get(sessionId);
        if (idx != null) idx.incrementAndGet();
    }

    // ─── Answer tracking ──────────────────────────────────────────────────────

    public int incrementAndGetAnswersCount(UUID sessionId) {
        AtomicInteger count = answersCount.get(sessionId);
        return count != null ? count.incrementAndGet() : 0;
    }

    public int getTotalQuestions(UUID sessionId) {
        Integer total = totalQuestions.get(sessionId);
        return total != null ? total : 0;
    }

    public boolean allAnswered(UUID sessionId) {
        AtomicInteger count = answersCount.get(sessionId);
        int answered = count != null ? count.get() : 0;
        return getTotalQuestions(sessionId) > 0 && answered >= getTotalQuestions(sessionId);
    }

    // ─── Rate limiting ────────────────────────────────────────────────────────

    public int getActiveSessionCount(UUID userId) {
        AtomicInteger count = userActiveCount.get(userId);
        return count != null ? count.get() : 0;
    }

    // ─── Cleanup ──────────────────────────────────────────────────────────────

    public void clearSession(UUID sessionId, UUID userId) {
        totalQuestions.remove(sessionId);
        currentIndex.remove(sessionId);
        answersCount.remove(sessionId);

        AtomicInteger count = userActiveCount.get(userId);
        if (count != null) {
            count.updateAndGet(v -> v > 0 ? v - 1 : 0);
        }
        log.info("Session cleared: sessionId={}", sessionId);
    }
}