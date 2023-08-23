package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.R;
import com.itheima.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author jsc
 * @version 1.0
 */
@Slf4j
@RequestMapping("/category")
@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    @PostMapping
    public R<String> sava(@RequestBody Category category) {
        categoryService.save(category);
        return R.success("分类保存成功");
    }

    @GetMapping("page")
    public R<Page> page(Integer page,Integer pageSize) {
        Page<Category> categoryPage = new Page<>(page,pageSize);
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByAsc(Category::getSort);
        categoryService.page(categoryPage,lambdaQueryWrapper);
        return R.success(categoryPage);
    }

    @DeleteMapping
    public R<String> delete(Long ids) {
        log.info("删除Category {}",ids);
        categoryService.remove(ids);
        return R.success("删除成功");
    }


    @PutMapping
    public R<String> update(@RequestBody Category category) {
       categoryService.updateById(category);
        return R.success("更新分类成功");
    }

    @GetMapping("/list")
    public R<List<Category>> getList(Category category) {
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(category.getType() != null,Category::getType,category.getType());
        lambdaQueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(lambdaQueryWrapper);
        return R.success(list);
    }


}
