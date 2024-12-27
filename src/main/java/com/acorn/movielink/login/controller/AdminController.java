package com.acorn.movielink.login.controller;

import com.acorn.movielink.login.dto.Member;
import com.acorn.movielink.login.dto.Notice;
import com.acorn.movielink.login.service.MemberService;
import com.acorn.movielink.login.service.NoticeService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final MemberService memberService;
    private final NoticeService noticeService;

    @Autowired
    public AdminController(MemberService memberService, NoticeService noticeService) {
        this.memberService = memberService;
        this.noticeService = noticeService;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "sort", required = false, defaultValue = "asc") String sort,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "nickname", required = false) String nickname,
            Model model) {

        PageInfo<Member> pageInfo = memberService.findMembers(sort, type, email, nickname, page, size);

        model.addAttribute("members", pageInfo.getList());
        model.addAttribute("currentPage", pageInfo.getPageNum());
        model.addAttribute("totalPages", pageInfo.getPages());
        model.addAttribute("size", pageInfo.getPageSize());
        model.addAttribute("sort", sort);
        model.addAttribute("type", type);
        model.addAttribute("email", email);
        model.addAttribute("nickname", nickname);

        return "dashboard";
    }

    // 유저 상세보기 페이지
    @GetMapping("/dashboard/{userId}")
    public String userDetails(@PathVariable("userId") Integer userId, Model model) {
        Optional<Member> memberOpt = memberService.findById(userId);
        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();
            model.addAttribute("member", member);
            return "user_details";
        } else {
            return "redirect:/admin/dashboard?error=UserNotFound";
        }
    }

    // 유저 수정 페이지 표시
    @GetMapping("/member/edit/{memId}")
    public String editMemberForm(@PathVariable("memId") Integer memId, Model model) {
        Optional<Member> memberOpt = memberService.findById(memId);
        if (memberOpt.isPresent()) {
            model.addAttribute("member", memberOpt.get());
            return "edit_member";
        } else {
            return "redirect:/admin/dashboard?error=UserNotFound";
        }
    }

    // 유저 수정 처리
    @PostMapping("/member/edit/{memId}")
    public String editMember(@PathVariable("memId") Integer memId,
                             @ModelAttribute Member updatedMember,
                             Model model) {
        updatedMember.setMemId(memId);
        memberService.updateMember(updatedMember);
        return "redirect:/admin/dashboard";
    }

    // 유저 삭제 처리
    @DeleteMapping("/member/delete/{memId}")
    @ResponseBody
    public ResponseEntity<Void> deleteMember(@PathVariable("memId") Integer memId) {
        memberService.deleteMember(memId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/notice")
    public String enrollNotice(Model model) {
        model.addAttribute("notice", new Notice());
        return "notice";
    }

    // 공지 작성 처리
    @PostMapping("/notice")
    public String createNotice(@ModelAttribute Notice notice, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            // 현재 로그인한 관리자의 memId 설정 (옵션)
            if (authentication != null && authentication.isAuthenticated()) {
                String email = authentication.getName();
                memberService.findByEmail(email).ifPresent(member -> notice.setMemId(member.getMemId()));
            }
            noticeService.createNotice(notice);
            redirectAttributes.addFlashAttribute("successMessage", "공지사항이 성공적으로 작성되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "공지사항 작성에 실패했습니다.");
            // 예외 로깅 추가 가능
        }
        return "redirect:/admin/noticelist";
    }

    // 공지 목록 조회
    @GetMapping("/noticelist")
    public String listNotices(Model model) {
        List<Notice> notices = noticeService.getAllNotices();
        model.addAttribute("notices", notices);
        return "notice_list";
    }

    // 공지 수정 페이지 표시
    @GetMapping("/notice/edit/{id}")
    public String editNoticeForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Notice> noticeOpt = noticeService.getNoticeById(id);
        if (noticeOpt.isPresent()) {
            model.addAttribute("notice", noticeOpt.get());
            return "edit_notice";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "공지사항을 찾을 수 없습니다.");
            return "redirect:/admin/noticelist";
        }
    }

    // 공지 수정 처리
    @PostMapping("/notice/edit/{id}")
    public String editNotice(@PathVariable("id") Integer id,
                             @ModelAttribute Notice updatedNotice,
                             RedirectAttributes redirectAttributes) {
        try {
            updatedNotice.setNotificationId(id);
            noticeService.updateNotice(updatedNotice);
            redirectAttributes.addFlashAttribute("successMessage", "공지사항이 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "공지사항 수정에 실패했습니다.");
            // 예외 로깅 추가 가능
        }
        return "redirect:/admin/noticelist";
    }

    // 공지 삭제 처리
    @PostMapping("/notice/delete/{id}")
    public String deleteNotice(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            noticeService.deleteNotice(id);
            redirectAttributes.addFlashAttribute("successMessage", "공지사항이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "공지사항 삭제에 실패했습니다.");
            // 예외 로깅 추가 가능
        }
        return "redirect:/admin/noticelist";
    }
}
