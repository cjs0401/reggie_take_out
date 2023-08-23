package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.entity.R;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
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
@RequestMapping("/dish")
@RestController
public class DishController {

    @Autowired
    private DishService dishService;


    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishFlavorService dishFlavorService;


    @PostMapping
    public R<String> save(@RequestBody DishDto dto) {
        log.info("新增菜品: ,{}",dto);
        dishService.saveWithFlavor(dto);
        return R.success("新增菜品成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name) {
//        封装dish 分页
        Page<Dish> dishPage = new Page<>(page,pageSize);
//        封装dish + category 分页
        Page<DishDto> dishDtoPage = new Page<>();

        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(name != null,Dish::getName,name);
        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
//        执行分页查询
        dishService.page(dishPage,lambdaQueryWrapper);

//        封装dish + category 分页
        BeanUtils.copyProperties(dishPage,dishDtoPage,"records");
        List<Dish> records = dishPage.getRecords();
        List<DishDto> collect = records.stream().map((item -> {
            DishDto dishDto = new DishDto();
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                dishDto.setCategoryName(category.getName());
            }
            BeanUtils.copyProperties(item, dishDto);
            return dishDto;
        })).collect(Collectors.toList());
//        设置回去dishDtoPage
        dishDtoPage.setRecords(collect);
        return R.success(dishDtoPage);
    }


    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id) {
        DishDto dishDto = dishService.getDishWithFlavorById(id);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dto) {
        log.info("更新菜品: ,{}",dto);
        dishService.updateWithFlavor(dto);
        return R.success("新增菜品成功");
    }


    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        wrapper.eq(Dish::getStatus,1);
        wrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(wrapper);

        List<DishDto> collect = list.stream().map((item -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DishFlavor::getDishId,item.getId());
            List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
            dishDto.setFlavors(flavors);
            return dishDto;
        })).collect(Collectors.toList());
//        设置回去dishDtoPage
        return R.success(collect);
    }


}
