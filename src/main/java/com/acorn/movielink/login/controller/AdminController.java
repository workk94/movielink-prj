package com.acorn.movielink.login.controller;

import com.acorn.movielink.login.dto.Member;
import com.acorn.movielink.login.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {


    private final MemberService memberService;

    @Autowired
    public AdminController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        List<Member> members = memberService.findAllMembers();
        model.addAttribute("members", members);
        return "dashboard";
    }

}
