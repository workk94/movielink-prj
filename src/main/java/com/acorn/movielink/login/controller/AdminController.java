package com.acorn.movielink.login.controller;

import com.acorn.movielink.login.dto.Member;
import com.acorn.movielink.login.service.MemberService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final MemberService memberService;

    @Autowired
    public AdminController(MemberService memberService) {
        this.memberService = memberService;
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

    @GetMapping("/notice")
    public String enrollNotice() {
        return "notice";
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


}
