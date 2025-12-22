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
// 标签选择器 - 简化版
class TagSelector {
    constructor() {
        // 预定义标签数据
        this.PREDEFINED_TAGS = {
            technology: ["java", "python", "javascript", "html", "css", "mysql", "redis", "docker"],
            framework: ["spring", "springboot", "vue", "react", "django", "flask"],
            language: ["java", "python", "javascript", "go", "c++", "php"],
            other: ["算法", "数据结构", "面试", "学习", "求助"]
        };

        this.POPULAR_TAGS = ["java", "spring", "mysql", "python", "算法", "面试"];

        this.selectedTags = [];
        this.MAX_TAGS = 5;

        this.currentCategory = 'technology';
    }

    // 初始化
    init() {
        console.log('初始化标签选择器...');

        // 1. 加载已有的标签
        this.loadExistingTags();

        // 2. 绑定事件
        this.bindEvents();

        // 3. 渲染初始内容
        this.renderTagCategory('technology');
        this.renderPopularTags();
        this.renderSelectedTags();
        this.updateSelectedCount();

        console.log('标签选择器初始化完成');
    }

    // 加载已有的标签
    loadExistingTags() {
        const tagInput = document.getElementById('tag');
        if (tagInput && tagInput.value) {
            this.selectedTags = tagInput.value.split(',')
                .map(tag => tag.trim())
                .filter(tag => tag.length > 0);
            console.log('加载已有标签:', this.selectedTags);
        }
    }

    // 绑定所有事件
    bindEvents() {
        console.log('绑定事件...');

        // 分类标签点击事件
        const categoryButtons = document.querySelectorAll('#tagCategoryTabs .nav-link');
        categoryButtons.forEach(button => {
            button.addEventListener('click', (e) => {
                e.preventDefault();

                // 移除所有active类
                categoryButtons.forEach(btn => btn.classList.remove('active'));

                // 添加active类到当前按钮
                button.classList.add('active');

                // 获取分类并渲染
                const category = button.getAttribute('data-category');
                this.currentCategory = category;
                this.renderTagCategory(category);
            });
        });

        // 搜索框输入事件
        const searchInput = document.getElementById('tagSearch');
        if (searchInput) {
            searchInput.addEventListener('input', (e) => {
                const keyword = e.target.value.trim();
                if (keyword) {
                    this.searchTags(keyword);
                } else {
                    this.renderTagCategory(this.currentCategory);
                }
            });
        }

        // 表单提交验证
        const form = document.getElementById('publishForm');
        if (form) {
            form.addEventListener('submit', (e) => {
                if (!this.validate()) {
                    e.preventDefault();
                }
            });
        }
    }

    // 渲染标签分类
    renderTagCategory(category) {
        console.log('渲染分类:', category);
        const tagList = document.getElementById('tagList');
        if (!tagList) return;

        const tags = this.PREDEFINED_TAGS[category] || [];
        let html = '';

        tags.forEach(tag => {
            const isSelected = this.selectedTags.includes(tag);
            const isDisabled = this.selectedTags.length >= this.MAX_TAGS && !isSelected;

            html += `
                <div class="tag-option ${isSelected ? 'selected' : ''} ${isDisabled ? 'disabled' : ''}" 
                     onclick="tagSelector.handleTagClick('${tag}')">
                     <i class="bi bi-tag"></i>
                     <span>${tag}</span>
                    <span class="check-mark">✓</span>
                </div>
            `;
        });

        tagList.innerHTML = html || '<div class="text-muted">暂无标签</div>';
    }

    // 渲染热门标签
    renderPopularTags() {
        const container = document.getElementById('popularTags');
        if (!container) return;

        let html = '';
        this.POPULAR_TAGS.forEach(tag => {
            const isSelected = this.selectedTags.includes(tag);
            html += `
                <div class="popular-tag ${isSelected ? 'selected' : ''}" 
                     onclick="tagSelector.handleTagClick('${tag}')">
                     <i class="bi bi-fire"></i>
                     <span>${tag}</span>
                </div>
            `;
        });

        container.innerHTML = html;
    }

    // 渲染已选标签
    renderSelectedTags() {
        const container = document.getElementById('selectedTags');
        if (!container) return;

        if (this.selectedTags.length === 0) {
            container.innerHTML = '<span class="text-muted" id="noTagsHint">请从下方选择标签（最多可选5个）</span>';
            return;
        }

        let html = '';
        this.selectedTags.forEach(tag => {
            html += `
                <div class="selected-tag-item">
                    <span class="remove-tag" onclick="tagSelector.removeTag('${tag}')">
                        <i class="bi bi-tag"></i>
                    </span>
                    <span>${tag}</span>
                </div>
            `;
        });

        container.innerHTML = html;
    }

    // 搜索标签
    searchTags(keyword) {
        const tagList = document.getElementById('tagList');
        if (!tagList) return;

        // 在所有标签中搜索
        const allTags = Object.values(this.PREDEFINED_TAGS).flat();
        const filteredTags = [...new Set(allTags)].filter(tag =>
            tag.toLowerCase().includes(keyword.toLowerCase())
        );

        if (filteredTags.length === 0) {
            tagList.innerHTML = '<div class="text-muted text-center py-3">未找到相关标签</div>';
            return;
        }

        let html = '';
        filteredTags.forEach(tag => {
            const isSelected = this.selectedTags.includes(tag);
            const isDisabled = this.selectedTags.length >= this.MAX_TAGS && !isSelected;

            html += `
                <div class="tag-option ${isSelected ? 'selected' : ''} ${isDisabled ? 'disabled' : ''}" 
                     onclick="tagSelector.handleTagClick('${tag}')">
                    ${tag}
                    <span class="check-mark">✓</span>
                </div>
            `;
        });

        tagList.innerHTML = html;
    }

    // 处理标签点击
    handleTagClick(tagName) {
        console.log('点击标签:', tagName);

        if (this.selectedTags.includes(tagName)) {
            this.removeTag(tagName);
        } else {
            this.addTag(tagName);
        }
    }

    // 添加标签
    addTag(tagName) {
        if (this.selectedTags.length >= this.MAX_TAGS) {
            this.showMessage(`最多只能选择 ${this.MAX_TAGS} 个标签`, 'warning');
            return;
        }

        if (!this.selectedTags.includes(tagName)) {
            this.selectedTags.push(tagName);
            this.updateUI();
            this.showMessage(`已添加标签: ${tagName}`, 'success');
        }
    }

    // 移除标签
    removeTag(tagName) {
        this.selectedTags = this.selectedTags.filter(tag => tag !== tagName);
        this.updateUI();
        this.showMessage(`已移除标签: ${tagName}`, 'info');
    }

    // 更新UI
    updateUI() {
        this.renderSelectedTags();
        this.updateSelectedCount();
        this.renderTagCategory(this.currentCategory);
        this.renderPopularTags();
        this.updateHiddenInput();
    }

    // 更新计数
    updateSelectedCount() {
        const countElement = document.getElementById('selectedCount');
        if (countElement) {
            countElement.textContent = this.selectedTags.length;
        }
    }

    // 更新隐藏输入
    updateHiddenInput() {
        const hiddenInput = document.getElementById('tag');
        if (hiddenInput) {
            hiddenInput.value = this.selectedTags.join(',');
        }
    }

    // 显示消息
    showMessage(message, type = 'info') {
        const alertClass = {
            'success': 'alert-success',
            'warning': 'alert-warning',
            'info': 'alert-info',
            'danger': 'alert-danger'
        }[type] || 'alert-info';

        // 创建消息元素
        const alertDiv = document.createElement('div');
        alertDiv.className = `alert ${alertClass} alert-dismissible fade show`;
        alertDiv.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;

        // 添加到表单前
        const form = document.getElementById('publishForm');
        if (form) {
            form.insertBefore(alertDiv, form.firstChild);

            // 3秒后自动移除
            setTimeout(() => {
                if (alertDiv.parentNode) {
                    alertDiv.remove();
                }
            }, 3000);
        }
    }

    // 验证
    validate() {
        if (this.selectedTags.length === 0) {
            this.showMessage('请至少选择一个标签', 'danger');
            return false;
        }

        if (this.selectedTags.length > this.MAX_TAGS) {
            this.showMessage(`标签数量不能超过 ${this.MAX_TAGS} 个`, 'danger');
            return false;
        }

        return true;
    }

    // 获取选中的标签
    getSelectedTags() {
        return this.selectedTags;
    }

    // 清除所有标签
    clearTags() {
        this.selectedTags = [];
        this.updateUI();
    }
}

// 创建全局实例并初始化
let tagSelector = null;

document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM加载完成，初始化标签选择器...');

    tagSelector = new TagSelector();
    tagSelector.init();

    // 将实例挂载到window对象以便测试
    window.tagSelector = tagSelector;

    console.log('标签选择器初始化状态:', {
        selectedTags: tagSelector.getSelectedTags(),
        currentCategory: tagSelector.currentCategory
    });
});

// 全局函数供HTML调用
function testTagSelector() {
    console.log('=== 测试标签选择器 ===');
    console.log('标签选择器实例:', window.tagSelector);
    console.log('当前选中的标签:', window.tagSelector?.getSelectedTags());
    console.log('当前分类:', window.tagSelector?.currentCategory);

    // 测试添加标签
    if (window.tagSelector) {
        window.tagSelector.addTag('测试标签');
    }
}
//删除问题
function deleteQuestion(e) {
    var questionId = e.getAttribute("data-id");
    console.log(questionId);
    var isDeleted = confirm("确认删除吗?");
    if (isDeleted) {
        window.location.replace("/profile/questions/delete/"+questionId);
    }


}