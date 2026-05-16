package com.sky.mapper;


import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    
    

    /**
     * 根据菜品id查询套餐id
     * @param dishIds
     * @return
     */
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

    /**
     * 向套餐菜品表插入数据
     */
    void insertBatch(List<SetmealDish> setmealDishes);
    /**
     * 删除套餐关系表数据
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据id查询套餐对应的菜品
     */
    @Select("select * from setmeal_dish where id = #{id}")
    List<SetmealDish> getBySetMealId(Long id);

    /**
     * 删除套餐和菜品关联的信息
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long setmealId);

}
