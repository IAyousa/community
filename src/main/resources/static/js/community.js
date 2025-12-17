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