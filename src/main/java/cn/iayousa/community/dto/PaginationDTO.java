package cn.iayousa.community.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PaginationDTO<T> {
    private List<T> data;
    private boolean showFirstPage;
    private boolean showEndPage;
    private boolean showPreviousPage;
    private boolean showNextPage;
    private Integer page;
    private List<Integer> pages = new ArrayList<>();
    private Integer totalPage;

    public void setPagination(Integer totalPage, Integer page) {
        this.page = page;
        this.totalPage = totalPage;

        //当前可展示分页设置
        //以当前页码为中心，向左右两侧添加页码
//        pages.add(page);
//        for(int i = 1; i <= 3; i++) {
//            //当 页码 - i > 0，向左侧添加页码
//            if(page - i > 0){
//                pages.add(0, page - i);
//            }
//            //当 页码 + i <= totalPage，向右侧添加页码
//            if(page + i <= totalPage){
//                pages.add(page + i);
//            }
//        }
        //与上述代码功能相同，但简化了添加逻辑
        for(int i = -3; i <= 3; i++){
            if(page + i < 1) continue;
            if(page + i > totalPage) break;
            pages.add(page+i);
        }

        //是否展示上一页按键
        if(page > 1){
            showPreviousPage = true;
        }
        else{
            showPreviousPage = false;
        }
        //是否展示下一页按键
        if(page < totalPage){
            showNextPage = true;
        }
        else{
            showNextPage = false;
        }
        //是否展示首页按键
        if(pages.contains(1)){
            showFirstPage = false;
        }
        else{
            showFirstPage = true;
        }
        //是否展示末页按键
        if(pages.contains(totalPage)){
            showEndPage = false;
        }
        else{
            showEndPage = true;
        }
    }
}
