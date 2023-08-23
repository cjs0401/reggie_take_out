package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
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
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Transactional
    @Override
    public void saveWithFlavor(DishDto dto) {
       this.save(dto);
       Long dishId = dto.getId();
        List<DishFlavor> flavors = dto.getFlavors();
        flavors.stream().map(
                (item) ->{item.setDishId(dishId);
                return item;
                }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public DishDto getDishWithFlavorById(Long id) {
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(list);
        BeanUtils.copyProperties(dish,dishDto);
        return dishDto;
    }

    @Transactional
    @Override
    public void updateWithFlavor(DishDto dto) {
        this.updateById(dto);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dto.getId());
        dishFlavorService.remove(queryWrapper);
        List<DishFlavor> flavors = dto.getFlavors();
        flavors = flavors.stream().map((item -> {
            item.setDishId(dto.getId());
            return item;
        })).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }
}
