//package com.acorn.movielink.config;
//
//import jakarta.servlet.http.HttpSessionEvent;
//import jakarta.servlet.http.HttpSessionListener;
//import lombok.Getter;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
//import java.util.Collections;
//import java.util.HashSet;
//import java.util.Set;
//
//실시간 동접자
//@Component
//public class SessionListener implements HttpSessionListener {
//
//    // SLF4J 로거 생성
//    private static final Logger logger = LoggerFactory.getLogger(SessionListener.class);
//
//    // 동시 접근 가능한 Set으로 현재 활성화된 사용자 ID를 저장
//    @Getter
//    private static final Set<Integer> activeUsers = Collections.synchronizedSet(new HashSet<>());
//
//    // 사용자를 추가하는 메서드
//    public static void addUser(Integer memId) {
//        activeUsers.add(memId);
//        logger.info("사용자 추가: memId={}", memId);
//    }
//
//    // 사용자를 제거하는 메서드
//    public static void removeUser(Integer memId) {
//        activeUsers.remove(memId);
//        logger.info("사용자 제거: memId={}", memId);
//    }
//
//    @Override
//    public void sessionCreated(HttpSessionEvent se) {
//        // 세션 생성 시 로깅
//        logger.info("세션 생성: {}", se.getSession().getId());
//    }
//
//    @Override
//    public void sessionDestroyed(HttpSessionEvent se) {
//        // 세션 종료 시 해당 사용자를 activeUsers에서 제거
//        Integer memId = (Integer) se.getSession().getAttribute("memId");
//        if (memId != null) {
//            activeUsers.remove(memId);
//            logger.info("세션 종료: {}, 사용자 ID: {}", se.getSession().getId(), memId);
//        } else {
//            logger.info("세션 종료: {} (memId 없음)", se.getSession().getId());
//        }
//    }
//}
