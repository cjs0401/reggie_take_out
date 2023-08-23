package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.R;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetMealDishService;
import com.itheima.reggie.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jsc
 * @version 1.0
 */
@Slf4j
@RequestMapping("/setmeal")
@RestController
public class SetMealController {

    @Autowired
    private SetMealService setMealService;

    @Autowired
    private SetMealDishService setMealDishService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("新增套餐信息: {}",setmealDto);
        setMealService.saveSetMealDish(setmealDto);
        return R.success("新增菜品成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name) {
        Page<Setmeal> SetmealPage = new Page<>(page,pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null,Setmeal::getName,name);
        wrapper.orderByAsc(Setmeal::getName).orderByDesc(Setmeal::getUpdateTime);
        setMealService.page(SetmealPage,wrapper);

        BeanUtils.copyProperties(SetmealPage,setmealDtoPage,"records");

        List<Setmeal> records = SetmealPage.getRecords();
        List<SetmealDto> collect = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            Category category = categoryService.getById(item.getCategoryId());
            if (category != null) {
                setmealDto.setCategoryName(category.getName());
            }
            BeanUtils.copyProperties(item, setmealDto);
            return setmealDto;
        }).collect(Collectors.toList());
        setmealDtoPage.setRecords(collect);
        return R.success(setmealDtoPage);
    }

    @DeleteMapping
    public R<String> delete(@RequestParam List<String> ids) {
        setMealService.removeWithDish(ids);
        return R.success("套餐数据删除成功！");
    }

    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        wrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        List<Setmeal> list = setMealService.list(wrapper);
        return R.success(list);
    }


}
