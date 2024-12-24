//package com.acorn.movielink.login.service;
//
//import com.acorn.movielink.config.SessionListener;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
// 실시간 동접자
//@Service
//public class ActiveUserService {
//
//    private static final Logger logger = LoggerFactory.getLogger(ActiveUserService.class);
//
//    private final SimpMessagingTemplate messagingTemplate;
//
//    @Autowired
//    public ActiveUserService(SimpMessagingTemplate messagingTemplate) {
//        this.messagingTemplate = messagingTemplate;
//    }
//
//    // 5초마다 활성 사용자 수를 클라이언트로 전송
//    @Scheduled(fixedRate = 300000)
//    public void sendActiveUserCount() {
//        int activeUserCount = SessionListener.getActiveUsers().size();
//        messagingTemplate.convertAndSend("/topic/active-user-count", activeUserCount);
//        logger.info("현재 활성 사용자 수: " + activeUserCount);
//    }
//
//    public int getActiveUserCount() {
//        int activeUserCount = SessionListener.getActiveUsers().size();
//        return activeUserCount;
//    }
//}
