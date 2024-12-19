package com.acorn.movielink.login.service;

import com.acorn.movielink.login.dto.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    // 임시 비밀번호 이메일 전송
    public void sendTemporaryPasswordEmail(Member member, String tempPassword) {
        logger.debug("임시 비밀번호 이메일 발송 요청 for 회원: {}", member.getMemEmail());
        String recipientAddress = member.getMemEmail();
        String subject = "임시 비밀번호 안내";
        String message = "안녕하세요, " + member.getMemNn() + "님.\n\n" +
                "비밀번호를 재설정하기 위해 임시 비밀번호를 생성하였습니다.\n" +
                "아래의 임시 비밀번호로 로그인하신 후, 반드시 비밀번호를 변경하시기 바랍니다.\n\n" +
                "임시 비밀번호: " + tempPassword + "\n\n" +
                "감사합니다.";

        SimpleMailMessage email = new SimpleMailMessage();
        // 발신자 주소 설정 (username@naver.com 형태로 Naver 메일 계정과 동일하게)
        email.setFrom("dkdjsema9808@naver.com");
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message);

        try {
            mailSender.send(email);
            logger.info("임시 비밀번호 이메일 발송 성공: {}", recipientAddress);
        } catch (Exception e) {
            logger.error("임시 비밀번호 이메일 발송 실패: {}", recipientAddress, e);
        }
    }
}
