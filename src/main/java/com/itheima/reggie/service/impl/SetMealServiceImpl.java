package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetMealMapper;
import com.itheima.reggie.service.SetMealDishService;
import com.itheima.reggie.service.SetMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jsc
 * @version 1.0
 */
@Service
public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, Setmeal> implements SetMealService {

    @Autowired
    private SetMealDishService setMealDishService;

    @Transactional
    @Override
    public void saveSetMealDish(SetmealDto setmealDto) {
        this.save(setmealDto);
        List<SetmealDish> collect = setmealDto.getSetmealDishes();
        collect = collect.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setMealDishService.saveBatch(collect);
    }


    @Transactional
    @Override
    public void removeWithDish(List<String> ids) {
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Setmeal::getId,ids);
        wrapper.eq(Setmeal::getStatus,1);
        int count = this.count(wrapper);
        if (count > 0) {
            throw new CustomException("套餐正在售卖中，不能删除！");
        }
//        先删除setmeal
        this.removeByIds(ids);
//        删除setmeal_dish
        LambdaQueryWrapper<SetmealDish> wrappers = new LambdaQueryWrapper<>();
        wrappers.in(SetmealDish::getSetmealId,ids);
        setMealDishService.remove(wrappers);
    }
}
