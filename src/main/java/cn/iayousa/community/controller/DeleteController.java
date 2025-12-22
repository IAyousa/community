package cn.iayousa.community.controller;

import cn.iayousa.community.dto.PaginationDTO;
import cn.iayousa.community.mapper.QuestionMapper;
import cn.iayousa.community.mapper.UserMapper;
import cn.iayousa.community.model.User;
import cn.iayousa.community.service.QuestionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Iterator;

@Controller
public class DeleteController {
    @Autowired
    private QuestionService questionService;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private UserMapper userMapper;
    @GetMapping("/profile/questions/delete/{id}")
    public String delete(@PathVariable(name = "id") Long id,
                         HttpServletRequest request,
                         Model model,
                         @RequestParam(name = "page", defaultValue = "1") Integer page,
                         @RequestParam(name = "size", defaultValue = "5") Integer size) {
        User user = (User) request.getSession().getAttribute("user");
        model.addAttribute("section", "questions");
        model.addAttribute("sectionName", "我的问题");
        questionMapper.deleteByPrimaryKey(id);
        PaginationDTO paginationDTO = questionService.list(user.getId(), page, size);
        model.addAttribute("pagination", paginationDTO);

        return "profile";
    }

}