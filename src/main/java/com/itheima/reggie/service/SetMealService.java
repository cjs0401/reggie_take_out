package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

import java.util.List;

/**
 * @author jsc
 * @version 1.0
 */
public interface SetMealService extends IService<Setmeal> {
    void saveSetMealDish(SetmealDto setmealDto);

    void removeWithDish(List<String> ids);
}
