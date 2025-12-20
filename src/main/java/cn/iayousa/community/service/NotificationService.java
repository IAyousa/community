package cn.iayousa.community.service;

import cn.iayousa.community.dto.NotificationDTO;
import cn.iayousa.community.dto.PaginationDTO;
import cn.iayousa.community.enums.NotificationStatusEnum;
import cn.iayousa.community.enums.NotificationTypeEnum;
import cn.iayousa.community.mapper.CommentMapper;
import cn.iayousa.community.mapper.NotificationMapper;
import cn.iayousa.community.mapper.QuestionMapper;
import cn.iayousa.community.mapper.UserMapper;
import cn.iayousa.community.model.*;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private CommentMapper  commentMapper;
    // 常量配置
    private static final Long SYSTEM_USER_ID = 0L;

    public PaginationDTO<NotificationDTO> list(Long userId, Integer page, Integer size){
        // 1. 参数校验
        if (page == null || page < 1) page = 1;

        // 2. 分页查询（复用你的分页逻辑）
        Integer offset = (page - 1) * size;
        if (offset < 0) offset = 0;

        NotificationExample example = new NotificationExample();
        example.createCriteria().andReceiverIdEqualTo(userId);
        example.setOrderByClause("gmt_create desc");

        RowBounds rowBounds = new RowBounds(offset, size);
        List<Notification> notifications = notificationMapper
                .selectByExampleWithRowbounds(example, rowBounds);

        // 3. 转换为DTO
        List<NotificationDTO> notificationDTOS = convertToDTOList(notifications);

        // 4. 计算分页信息
        NotificationExample countExample = new NotificationExample();
        countExample.createCriteria().andReceiverIdEqualTo(userId);
        Integer totalCount = (int) notificationMapper.countByExample(countExample);
        Integer totalPage = calculateTotalPage(totalCount, size);

        // 5. 校正页码
        page = correctPageNumber(page, totalPage);

        // 6. 封装分页结果
        PaginationDTO<NotificationDTO> paginationDTO = new PaginationDTO<>();
        paginationDTO.setPagination(totalPage, page);
        paginationDTO.setData(notificationDTOS);

        return paginationDTO;
    }
    //转换为DTO列表
    private List<NotificationDTO> convertToDTOList(List<Notification> notifications) {
        if (notifications == null || notifications.isEmpty()) {
            return Collections.emptyList();
        }
        // 提取所有通知者ID
        List<Long> notifierIds = new ArrayList<>(notifications.stream()
                .map(Notification::getNotifierId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet())
        );
        // 批量查询通知者信息
        UserExample userExample = new UserExample();
        userExample.createCriteria().andIdIn(notifierIds);
        List<User> users = userMapper.selectByExample(userExample);

        Map<Long, User> userMap = users.stream().
                collect(
                        Collectors.toMap(User::getId, user -> user)
                );

        // 转换为DTO并加载关联内容
        return notifications.stream()
                .map(notification -> {
                    NotificationDTO dto = new NotificationDTO();
                    BeanUtils.copyProperties(notification, dto);

                    // 设置通知者
                    if (userMap.containsKey(notification.getNotifierId())) {
                        dto.setNotifier(userMap.get(notification.getNotifierId()));
                    }

                    // 加载被回复的具体内容
                    loadRepliedContent(dto, notification);

                    return dto;
                })
                .collect(Collectors.toList());
    }
    //加载被回复的具体内容
    private void loadRepliedContent(NotificationDTO dto, Notification notification) {
        NotificationTypeEnum typeEnum = NotificationTypeEnum.getByType(notification.getType());
        if (typeEnum == null) return;

        try {
            switch (typeEnum) {
                case REPLY_QUESTION:
                    // 加载被回复的问题标题
                    Question question = questionMapper.selectByPrimaryKey(notification.getOuterId());
                    if (question != null) {
                        dto.setRepliedQuestionTitle(question.getTitle());
                    }
                    break;

                case REPLY_COMMENT:
                case LIKE_COMMENT:
                    // 加载被回复的评论内容
                    Comment comment = commentMapper.selectByPrimaryKey(notification.getOuterId());
                    if (comment != null) {
                        dto.setRepliedCommentContent(comment.getContent());
                    }
                    break;

                case SYSTEM_NOTICE:
                    // 系统通知内容就是通知内容本身
                    dto.setSystemNoticeContent(notification.getContent());
                    break;
            }
        } catch (Exception e) {
            // 记录错误但不影响主流程
            System.err.println("加载回复内容失败, notificationId: {}");
        }
    }
    //计算总页数
    private Integer correctPageNumber(Integer page, Integer totalPage) {
        if (page < 1) return 1;
        if (page > totalPage) return totalPage;
        return page;
    }
    //校正页码
    private Integer calculateTotalPage(Integer totalCount, Integer size) {
        Integer totalPage = totalCount / size;
        if (totalCount % size != 0) {
            totalPage++;
        }
        return totalPage;
    }


    //获取未读通知数量
    public Long unreadCount(Long userId){
        NotificationExample example = new NotificationExample();
        example.createCriteria()
                .andReceiverIdEqualTo(userId)
                .andStatusEqualTo(NotificationStatusEnum.UNREAD.getValue());
        return notificationMapper.countByExample(example);
    }

    //通用创建通知方法
    public void createNotification(NotificationTypeEnum typeEnum,
                                   Long notifierId,
                                   Long receiverId,
                                   Long outerId,
                                   String customContent){
        Notification notification = new Notification();
        notification.setNotifierId(notifierId);
        notification.setReceiverId(receiverId);
        notification.setOuterId(outerId);
        notification.setType(typeEnum.getType());
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setContent(generateContent(typeEnum, notifierId, outerId, customContent));
        notificationMapper.insertSelective(notification);
    }

    //创建回复问题通知
    public void createReplyQuestionNotification(Long notifierId, Long questionId) {
        Question question = questionMapper.selectByPrimaryKey(questionId);
        if (question != null) {
            createNotification(
                    NotificationTypeEnum.REPLY_QUESTION,
                    notifierId,
                    question.getCreatorId(),
                    questionId,
                    null
            );
        }
    }

    //创建回复评论通知
    public void createReplyCommentNotification(Long notifierId, Long commentId) {
        Comment comment = commentMapper.selectByPrimaryKey(commentId);
        Comment parentComment = commentMapper.selectByPrimaryKey(comment.getParentId());
        if (comment != null) {
            createNotification(
                    NotificationTypeEnum.REPLY_COMMENT,
                    notifierId,
                    comment.getCommentatorId(),
                    parentComment.getParentId(),
                    null
            );
        }
    }

    //创建点赞通知
    public void createLikeNotification(Long notifierId, Long commentId) {
        Comment comment = commentMapper.selectByPrimaryKey(commentId);
        if (comment != null) {
            createNotification(
                    NotificationTypeEnum.LIKE_COMMENT,
                    notifierId,
                    comment.getCommentatorId(),
                    commentId,
                    null
            );
        }
    }

    //创建系统通知
    public void createSystemNotification(Long receiverId, String content) {
        createNotification(
                NotificationTypeEnum.SYSTEM_NOTICE,
                SYSTEM_USER_ID,
                receiverId,
                0L,
                content
        );
    }

    // ==================== 状态更新方法 ====================

    //标记通知为已读/
    public void read(Long id, Long userId) {
        Notification notification = new Notification();
        notification.setId(id);
        notification.setStatus(NotificationStatusEnum.READ.getValue());

        NotificationExample example = new NotificationExample();
        example.createCriteria()
                .andIdEqualTo(id)
                .andReceiverIdEqualTo(userId);

        notificationMapper.updateByExampleSelective(notification, example);
    }

    //标记所有通知为已读
    public void readAll(Long userId) {
        Notification notification = new Notification();
        notification.setStatus(1);

        NotificationExample example = new NotificationExample();
        example.createCriteria().andReceiverIdEqualTo(userId);

        notificationMapper.updateByExampleSelective(notification, example);
    }

    //生成通知内容
    private String generateContent(NotificationTypeEnum typeEnum, Long notifierId, Long outerId, String customContent) {
        User notifier = userMapper.selectByPrimaryKey(notifierId);
        String operatorName = notifier != null ? notifier.getName() : "用户";

        switch (typeEnum) {
            case REPLY_QUESTION:
                Question question = questionMapper.selectByPrimaryKey(outerId);
                return question != null ?
                        operatorName + " 回复了你的问题: " + question.getTitle() :
                        operatorName + " 回复了你的问题";

            case REPLY_COMMENT:
                Comment comment = commentMapper.selectByPrimaryKey(outerId);
                return comment != null ?
                        operatorName + " 回复了你的评论: " + comment.getContent() :
                        operatorName + " 回复了你的评论";

            case LIKE_COMMENT:
                Comment likedComment = commentMapper.selectByPrimaryKey(outerId);
                return likedComment != null ?
                        operatorName + " 赞了你的评论: " + likedComment.getContent() :
                        operatorName + " 赞了你的评论";

            case SYSTEM_NOTICE:
                return customContent != null ? customContent : "系统通知";

            default:
                return "新通知";
        }
    }
}
