package com.acorn.movielink.login.service;

import com.acorn.movielink.config.PasswordUtil;
import com.acorn.movielink.login.dto.*;
import com.acorn.movielink.login.repository.MemberMapper;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    private static final Logger logger = LoggerFactory.getLogger(MemberService.class);

    @Getter
    private final PasswordEncoder passwordEncoder;
    private final MemberMapper memberMapper;
    private final EmailService emailService;
    private final GenreService genreService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    public MemberService(PasswordEncoder passwordEncoder, MemberMapper memberMapper, EmailService emailService, GenreService genreService) {
        this.passwordEncoder = passwordEncoder;
        this.memberMapper = memberMapper;
        this.emailService = emailService;
        this.genreService = genreService;
    }

    @Transactional
    public void registerMember(Member member) {
        logger.debug("회원가입 요청: {}", member);
        if (member.getMemPw() != null) {
            member.setMemPw(passwordEncoder.encode(member.getMemPw()));
        }
        memberMapper.insertMember(member);

        // memId 확인 및 로그 추가
        if (member.getMemId() == null) {
            throw new RuntimeException("회원가입 후 memId가 설정되지 않았습니다.");
        }
        logger.debug("회원가입 완료: memId={}, memEmail={}", member.getMemId(), member.getMemEmail());

        // 장르 선택 처리
        List<Integer> genreIds = member.getGenreIds();
        if (genreIds != null && !genreIds.isEmpty()) {
            for (Integer genreId : genreIds) {
                memberMapper.insertMemberGenre(genreId, member.getMemId());
            }
            logger.info("회원-장르 관계 저장 완료: 회원 ID={}, 장르 IDs={}", member.getMemId(), genreIds);
        }
    }

    public Optional<Member> findByEmail(String email) {
        logger.debug("이메일로 회원 검색 요청: {}", email);
        Optional<Member> member = memberMapper.findByMemEmail(email);
        logger.debug("쿼리 결과: {}", member);
        if (member.isPresent()) {
            logger.info("회원 검색 성공: {}", email);
        } else {
            logger.warn("회원 검색 실패: {}", email);
        }
        return member;
    }

    public Optional<Member> findByMemSnsId(String memSnsId) {
        logger.debug("memSnsId로 회원 검색 요청: {}", memSnsId);
        Optional<Member> member = memberMapper.findByMemSnsId(memSnsId);
        logger.debug("쿼리 결과: {}", member);
        if (member.isPresent()) {
            logger.info("회원 검색 성공: {}", memSnsId);
        } else {
            logger.warn("회원 검색 실패: {}", memSnsId);
        }
        return member;
    }

    public void updatePassword(Integer memId, String newPassword) {
        logger.debug("비밀번호 업데이트 요청 for 회원 ID: {}", memId);
        String encodedPassword = passwordEncoder.encode(newPassword);
        memberMapper.updatePassword(memId, encodedPassword);
        logger.info("비밀번호 업데이트 완료 for 회원 ID: {}", memId);
    }

    public String resetPasswordAndSendEmail(String email) {
        logger.debug("비밀번호 재설정 요청: {}", email);
        Optional<Member> memberOpt = findByEmail(email);
        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();
            String tempPassword = PasswordUtil.generateRandomPassword();
            updatePassword(member.getMemId(), tempPassword);
            emailService.sendTemporaryPasswordEmail(member, tempPassword);
            logger.info("임시 비밀번호 생성 및 이메일 발송 완료 for 이메일: {}", email);
            return tempPassword;
        }
        logger.warn("비밀번호 재설정 실패 - 존재하지 않는 이메일: {}", email);
        return null;
    }

    @Transactional
    public void updateMember(Member updatedMember) {
        logger.debug("회원 정보 업데이트 요청 for 회원 ID: {}", updatedMember.getMemId());
        memberMapper.updateMember(updatedMember);
        logger.info("회원 정보 업데이트 완료 for 회원 ID: {}", updatedMember.getMemId());
    }

    @Transactional
    public void deleteMember(Integer memId) {
        logger.debug("회원 삭제 요청 for 회원 ID: {}", memId);
        memberMapper.deleteMember(memId);
        logger.info("회원 삭제 완료 for 회원 ID: {}", memId);
    }

    @Transactional
    public void updateMemberGenres(Integer memId, List<Integer> genreIds) {
        logger.debug("기존 장르 삭제 요청 for 회원 ID: {}", memId);
        memberMapper.deleteMemberGenres(memId);

        if (genreIds != null && !genreIds.isEmpty()) {
            for (Integer genreId : genreIds) {
                // 장르 존재 여부 확인
                if (genreService.getGenreById(genreId).isPresent()) {
                    memberMapper.insertMemberGenre(genreId, memId);
                } else {
                    logger.warn("존재하지 않는 장르 ID: {}", genreId);
                    throw new IllegalArgumentException("존재하지 않는 장르 ID가 포함되어 있습니다.");
                }
            }
            logger.info("회원-장르 관계 업데이트 완료: 회원 ID={}, 장르 IDs={}", memId, genreIds);
        }
    }

    public Optional<Member> findById(Integer memId) {
        return memberMapper.findByMemId(memId);
    }

    public List<Integer> getGenreIdsByMemberId(Integer memId) {
        return memberMapper.findGenreIdsByMemId(memId);
    }

    // 파일 저장 메서드 추가
    public String saveProfileImage(MultipartFile file, Integer memId) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("파일이 비어 있습니다.");
        }

        // 파일 이름 정리
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = getFileExtension(originalFilename);
        String newFileName = "profile_" + memId + "_" + System.currentTimeMillis() + "." + fileExtension;

        // 파일 저장 경로 설정
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 파일 저장
        Path filePath = uploadPath.resolve(newFileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        logger.info("프로필 이미지 업로드 성공: {}", newFileName);
        // 웹 접근 가능한 URL 경로 반환
        return "/uploads/" + newFileName;
    }

    // 파일 삭제 메서드 추가
    public void deleteProfileImage(String convertedFileName) {
        if (convertedFileName == null || convertedFileName.isEmpty()) {
            return;
        }

        Path filePath = Paths.get(uploadDir).resolve(convertedFileName).normalize();
        try {
            Files.deleteIfExists(filePath);
            logger.info("프로필 이미지 삭제 성공: {}", convertedFileName);
        } catch (IOException e) {
            logger.error("프로필 이미지 삭제 실패: {}", convertedFileName, e);
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex >= 0) ? filename.substring(dotIndex + 1) : "";
    }

    // 로그인 시 memType 확인 후 리다이렉션 설정
    private boolean checkMemType(Byte memType) {
        // memType이 1이면 false (관리자)
        return memType != 1;
    }

    // 좋아요한 인물 조회 메서드 추가
    public List<Person> getLikedPersons(Integer memId) {
        return memberMapper.findLikedPersonsByMemId(memId);
    }

    // 작성글 조회 메서드 추가
    public List<Post> getWrittenPosts(Integer memId) {
        return memberMapper.findWrittenPostsByMemId(memId);
    }

    // 좋아요한 영화 조회 메서드 추가
    public List<Movie> getLikedMovies(Integer memId) {
        return memberMapper.findLikedMoviesByMemId(memId);
    }

    // 구매한 아이템 조회 메서드 추가
    public List<Item> getPurchasedItems(Integer memId) {
        return memberMapper.findPurchasedItemByMemId(memId);
    }


    // 사용자 장르에 맞는 10개 영화 추천
    public List<Movie> getRecommendedMovies(Integer memId) {
        logger.debug("사용자 장르에 맞는 추천 영화 10개 조회 요청 for 회원 ID: {}", memId);
        List<Movie> recommendedMovies = memberMapper.findRecommendedMovies(memId);
        logger.debug("쿼리 결과: {}", recommendedMovies);
        return recommendedMovies;
    }
}
