package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;

/**
 * @author jsc
 * @version 1.0
 */

public interface DishService extends IService<Dish> {
    void saveWithFlavor(DishDto dto);

    DishDto getDishWithFlavorById(Long id);

    void updateWithFlavor(DishDto dto);
}
