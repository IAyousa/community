package cn.iayousa.community.service;

import cn.iayousa.community.dto.PaginationDTO;
import cn.iayousa.community.dto.QuestionDTO;
import cn.iayousa.community.mapper.QuestionMapper;
import cn.iayousa.community.mapper.UserMapper;
import cn.iayousa.community.model.Question;
import cn.iayousa.community.model.QuestionExample;
import cn.iayousa.community.model.User;
import cn.iayousa.community.model.UserExample;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
//Service 类封装业务流程，或者说是纯粹的界面上的业务流程
//用于管理question表的业务操作
@Service
public class QuestionService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionMapper questionMapper;

    public PaginationDTO list(Integer page, Integer size) {
        Integer offset = (page - 1) * size;
        if (offset < 0){
            offset = 0;
        }
        List<Question> questions = questionMapper.selectByExampleWithBLOBsWithRowbounds(
                new QuestionExample(), new RowBounds(offset, size)
            );
        List<QuestionDTO> questionDTOS = new ArrayList<>();
        PaginationDTO paginationDTO = new PaginationDTO();

        Integer totalCount = (int) questionMapper.countByExample(new QuestionExample());
        Integer totalPage = totalCount / size + (totalCount % size == 0 ? 0 : 1);
        if(page < 1) {page = 1;}
        if(page > totalPage) {page = totalPage;}
        paginationDTO.setPagination(totalPage, page);

        for (Question question : questions) {
            UserExample userExample = new UserExample();
            userExample.createCriteria().
                    andIdEqualTo(question.getCreatorId());
            User user = userMapper.selectByExample(userExample).get(0);
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOS.add(questionDTO);
        }

        paginationDTO.setData(questionDTOS);

        return paginationDTO;
    }

    public PaginationDTO list(Integer userId, Integer page, Integer size) {
        Integer offset = (page - 1) * size;
        if (offset < 0){
            offset = 0;
        }
        QuestionExample example = new QuestionExample();
        example.createCriteria().andCreatorIdEqualTo(userId);
        RowBounds rowBounds = new RowBounds(offset, size);
        List<Question> questions = questionMapper.selectByExampleWithBLOBsWithRowbounds(example, rowBounds);
        List<QuestionDTO> questionDTOS = new ArrayList<>();
        PaginationDTO paginationDTO = new PaginationDTO();

        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria().andCreatorIdEqualTo(userId);
        Integer totalCount = (int) questionMapper.countByExample(questionExample);
        Integer totalPage = totalCount / size + (totalCount % size == 0 ? 0 : 1);
        if(page < 1) {page = 1;}
        if(page > totalPage) {page = totalPage;}
        paginationDTO.setPagination(totalPage, page);

        for (Question question : questions) {
            UserExample userExample = new UserExample();
            userExample.createCriteria().
                    andIdEqualTo(question.getCreatorId());
            User user = userMapper.selectByExample(userExample).get(0);
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOS.add(questionDTO);
        }

        paginationDTO.setData(questionDTOS);

        return paginationDTO;
    }

    public QuestionDTO findById(Integer id) {
        Question question = questionMapper.selectByPrimaryKey(id);
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question, questionDTO);UserExample userExample = new UserExample();
        userExample.createCriteria().
                andIdEqualTo(question.getCreatorId());
        User user = userMapper.selectByExample(userExample).get(0);
        questionDTO.setUser(user);
        return questionDTO;
    }

    public void createOrUpdate(Question question) {
        if(question.getId() == null){
            //新创建的问题
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            questionMapper.insertSelective(question);
        }
        else{
            //已创建的问题
            question.setGmtModified(question.getGmtCreate());
            questionMapper.updateByPrimaryKeySelective(question);
        }
    }
}
