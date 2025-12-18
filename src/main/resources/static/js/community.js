async function post(){
    const questionId = document.getElementById('question_id').value;
    const content = document.getElementById('floatingTextarea2').value;
    const button = document.querySelector('.btn-publish');

    if (!content.trim()) {
        alert('请填写回复内容');
        return;
    }

    // 禁用按钮，防止重复提交
    button.disabled = true;
    button.textContent = '提交中...';

    try {
        const response = await fetch('/comment', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                parentId: questionId,
                content: content,
                type: 1
            })
        });

        const data = await response.json();
        //
        // if (response.ok && data.success) {
        //     // 1. 清空文本框
        //     document.getElementById('floatingTextarea2').value = '';
        //
        //     // 2. 创建新评论元素
        //     const newComment = createCommentElement(data.comment);
        //
        //     // 3. 插入到评论列表开头（假设有评论列表容器）
        //     const commentList = document.getElementById('comment-list');
        //     if (commentList) {
        //         commentList.insertBefore(newComment, commentList.firstChild);
        //     } else {
        //         // 如果没有评论列表，先创建
        //         createCommentList(newComment);
        //     }
        //
        //     // 4. 更新评论计数
        //     updateCommentCount();
        //
        //     alert('回复成功！');
        // } else {
        //     alert(data.message || '回复失败');
        // }
        if(data.code == 200){
            document.getElementById('floatingTextarea2').value = '';
            alert('回复成功！');
            window.location.reload();
        }
        else{
            // 状态码非2xx，根据具体状态码处理
            await handleHttpError(data);
        }
    } catch (error) {
        console.error('请求失败:', error);
        alert('网络错误，请稍后重试');
    } finally {
        // 恢复按钮
        button.disabled = false;
        button.textContent = '回复';
    }
    console.log(questionId);
    console.log(content);
}

async function handleHttpError(response) {
    if(response.code === 5004){
        var isAccepted = confirm(response.message);
        if(isAccepted){
            window.open("https://github.com/login/oauth/authorize?client_id=Ov23liS8x0g5m1E0K2xb&redirect_uri=http://localhost:8080/callback&scope=user&state=1");
            localStorage.setItem("closable",  "true");
        }

    }
}

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    // 1. 监听所有二级评论区域的展开事件（用于懒加载）
    document.querySelectorAll('.sub-comments').forEach(collapseEl => {
        collapseEl.addEventListener('show.bs.collapse', handleCommentExpand);
    });

    // 2. 回复按钮事件委托
    document.addEventListener('click', function(e) {
        if (e.target.classList.contains('btn-reply') ||
            e.target.closest('.btn-reply')) {
            e.preventDefault();
            const button = e.target.classList.contains('btn-reply') ?
                e.target : e.target.closest('.btn-reply');
            replyToComment(button);
        }
    });
});

/**
 * 处理评论展开事件 - 懒加载二级评论
 */
function handleCommentExpand(event) {
    const collapseElement = event.target;
    const commentId = collapseElement.id.replace('collapse-', '');
    const container = document.getElementById('sub-comments-' + commentId);
    const button = document.querySelector(`[data-id="${commentId}"]`);

    // 检查是否已经加载过
    if (!container || button?.hasAttribute('data-loaded')) {
        return;
    }

    // 加载二级评论
    loadSubComments(commentId, container, button);
}

/**
 * 加载二级评论数据
 */
async function loadSubComments(commentId, container, button) {
    try {
        // 显示加载状态
        container.innerHTML = '<div class="text-center text-muted py-3">加载中...</div>';

        const response = await fetch(`/comment/${commentId}`);
        const data = await response.json();

        if (data.code === 200 && data.data) {
            renderSubComments(container, data.data);

            // 标记已加载
            if (button) button.setAttribute('data-loaded', 'true');
        } else {
            throw new Error(data.message || '加载失败');
        }
    } catch (error) {
        console.error('加载二级评论失败:', error);
        container.innerHTML = `
            <div class="text-center text-danger py-3">
                加载失败，请刷新页面重试
            </div>
        `;
    }
}

/**
 * 渲染二级评论到容器
 */
function renderSubComments(container, comments) {
    if (!comments || comments.length === 0) {
        container.innerHTML = `
            <div class="text-center text-muted py-3">
                <i class="bi bi-chat-quote"></i> 暂无回复
            </div>
        `;
        return;
    }

    let html = '';
    comments.forEach(comment => {
        const date = new Date(comment.gmtCreate).toLocaleDateString('zh-CN');
        html += `
            <div class="d-flex media-block mb-3">
                <div class="flex-shrink-0">
                    <img src="${comment.user.avatarUrl}" 
                         class="rounded avatar-img-sm" 
                         alt="${comment.user.name}"
                         style="width: 32px; height: 32px; object-fit: cover;">
                </div>
                <div class="flex-grow-1 ms-3">
                    <small class="text-body-secondary">
                        <span>${escapeHtml(comment.user.name)}</span>
                    </small>
                    <br>
                    <span>${escapeHtml(comment.content)}</span>
                    <br>
                    <small class="text-body-secondary comment-util">
                        <button class="btn btn-sm" type="button">
                            <i class="bi bi-hand-thumbs-up icon"></i>
                            <span class="count">${comment.likeCount || 0}</span>
                        </button>
                        <span class="float-end">${date}</span>
                    </small>
                </div>
            </div>
            <hr class="my-2">
        `;
    });

    container.innerHTML = html;
}

/**
 * 回复二级评论
 */
async function replyToComment(button) {
    const commentId = button.getAttribute('data-id');
    const input = document.getElementById('input-' + commentId);
    const content = input.value.trim();

    if (!content) {
        alert('请输入回复内容');
        input.focus();
        return;
    }

    // 保存原始状态
    const originalText = button.textContent;
    button.disabled = true;
    button.textContent = '提交中...';

    try {
        const response = await fetch('/comment', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                parentId: commentId,
                content: content,
                type: 2
            })
        });

        const data = await response.json();

        if (data.code === 200) {
            input.value = '';
            alert('回复成功！');

            // 获取相关元素
            const container = document.getElementById('sub-comments-' + commentId);
            const toggleButton = document.querySelector(`[data-id="${commentId}"]`);

            // 更新评论数量
            if (toggleButton) {
                const countSpan = toggleButton.querySelector('span');
                if (countSpan) {
                    const currentCount = parseInt(countSpan.textContent) || 0;
                    countSpan.textContent = currentCount + 1;
                }
            }

            // 如果二级评论区域是展开的，重新加载数据
            const collapseElement = document.getElementById('collapse-' + commentId);
            if (collapseElement.classList.contains('show')) {
                // 标记需要重新加载
                if (toggleButton) toggleButton.removeAttribute('data-loaded');
                // 重新加载
                loadSubComments(commentId, container, toggleButton);
            } else {
                // 如果没展开，刷新页面
                window.location.reload();
            }
        } else {
            handleHttpError(data);
        }
    } catch (error) {
        console.error('回复失败:', error);
        alert('网络错误，请稍后重试');
    } finally {
        button.disabled = false;
        button.textContent = originalText;
    }
}

/**
 * HTML转义函数
 */
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

/**
 * 已有的错误处理函数
 */
async function handleHttpError(response) {
    if(response.code === 5004){
        var isAccepted = confirm(response.message);
        if(isAccepted){
            window.open("https://github.com/login/oauth/authorize?client_id=Ov23liS8x0g5m1E0K2xb&redirect_uri=http://localhost:8080/callback&scope=user&state=1");
            localStorage.setItem("closable", "true");
        }
    }
}