package cn.iayousa.community.controller;

import cn.iayousa.community.dto.CommentDTO;
import cn.iayousa.community.dto.QuestionDTO;
import cn.iayousa.community.service.CommentService;
import cn.iayousa.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class QuestionController {
    @Autowired
    QuestionService questionService;
    @Autowired
    private CommentService commentService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id") Long id,
                           Model model){
        QuestionDTO questionDTO = questionService.findById(id);
        List<CommentDTO> comments = commentService.listById(id);
        questionService.incViewCount(id);
        model.addAttribute("question", questionDTO);
        model.addAttribute("comments", comments);
        return "question";
    }
}
