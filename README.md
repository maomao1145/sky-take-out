## 新增菜品
---
```DishController
@PostMapping  
@ApiOperation("新增菜品")  
public Result save(@RequestBody DishDTO dishDTO){  
    log.info("新增菜品: {}",dishDTO);  
    dishService.saveWithFlavor(dishDTO);  
    return Result.success();  
}
```

```DishService
public void saveWithFlavor(DishDTO dishDTO);
```

```DishServiceImpl
public void saveWithFlavor(DishDTO dishDTO){  
    Dish dish = new Dish();  
  
    BeanUtils.copyProperties(dishDTO,dish);  
    //向菜品表插入一条数据  
  
    dishMapper.insert(dish);  
  
    //获取insert语句生成的主键值  
    Long dishId = dish.getId();  
  
  
    List<DishFlavor> flavors = dishDTO.getFlavors();  
    if(flavors != null && flavors.size() > 0){  
        flavors.forEach(dishFlavor -> {  
            dishFlavor.setDishId(dishId);  
        });        //向口味表插入n条数据  
        dishFlavorMapper.insertBatch(flavors);  
        }  
    }
```

```DishMapper.java
@AutoFill(value = OperationType.INSERT)  
void insert(Dish dish);
```

```DishMapperXMl
<insert id="insert" useGeneratedKeys="true" keyProperty="id">  
    insert into dish (name, category_id, price, image, description, create_time, update_time, create_user, update_user,status)  
        values  
    (#{name}, #{categoryId}, #{price}, #{image}, #{description}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser}, #{status})  
</insert>
```

```DishFlavorMapperXML
<insert id="insertBatch">    //插入口味相关数据
    insert into dish_flavor (dish_id, name, value) values  
    <foreach collection="flavors" item="df" separator=",">  
        (#{df.dishId},#{df.name},#{df.value})  
    </foreach>  
</insert>

```

---
## 分类查询
```DishController
@GetMapping("/page")  
@ApiOperation("菜品分类查询")  
public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){  
    log.info("菜品分类查询: {}", dishPageQueryDTO);  
    PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);  
    return Result.success(pageResult);  
}
```

```DishService
PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);
```

```DishServiceImpl
public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO){  
    PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());  
    Page<DishVO> page =dishMapper.pageQuery(dishPageQueryDTO);  
    return new PageResult(page.getTotal(),page.getResult());  
}
```

```DishMapper.java
Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);
```

```DishMapperXML
<select id="pageQuery" resultType="com.sky.vo.DishVO">  
    select d.*,c.name as categoryName from dish d left outer join category c on d.category_id = c.id  //采用左外连接查询数据 name 重复 name as categoryName
    <where>  
        <if test="name != null and name != ''">  //排除空字符串
            and d.name like concat('%',#{name},'%')  
        </if>  
        <if test="categoryId != null">  
            and d.category_id = #{categoryId}  
        </if>  
        <if test="status != null">  
            and d.status = #{status}  
        </if>  
    </where>  
        order by d.create_time desc  //根据创建时间降序排列
  
</select>
```

//运行是返回500错误很有可能是sql数据出错
//401表示jwt令牌已过期
## 删除菜品

---

```DishController
// 菜品的批量删除  
@DeleteMapping  
@ApiOperation("菜品的批量删除")  
public Result delete(@RequestParam List<Long> ids){  
    log.info("菜品的批量删除: {}", ids);  
    dishService.deleteBatch(ids);  
    return Result.success();  
}
```

```DishService
void deleteBatch(List<Long> ids);
```

这里为主要的代码 调用到SetmealDishMapper
```DishServiceImpl
public void deleteBatch(List<Long> ids) {  
        //判断当前的菜品是否能够删除 是否存在启售中的菜品? status  
        for (Long id : ids) {  
            Dish dish = dishMapper.getById(id);  
            if(dish.getStatus() == StatusConstant.ENABLE){  
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);  
            }        }  
        //判断当前菜品是否能够删除 是否被套餐关联了  
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);  
        if(setmealIds != null && setmealIds.size() > 0){  
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);  
        }  
//        //删除菜品表中的数据  
//        for (Long id : ids) {  
//            dishMapper.deleteById(id);  
//            //删除菜品关联的口味数据  
//            dishFlavorMapper.deleteByDishId(id);  
//        }  
//如果ids很多性能也会很大  
//代码优化  
        //根据菜品集合删除菜品表的数据 
         
        dishMapper.deleteByIds(ids);  
        //根据菜品集合删除菜品关联的口味数据  
        
        dishFlavorMapper.deleteByDishIds(ids);  
  
    }
```

```DishMapper.java
@Delete("delete from dish where id = #{id}" )  
void deleteById(Long id);  
 //优化为 批量删除 
void deleteByIds(List<Long> ids);
```

```DishMapperXml
<delete id="deleteByIds">  
    delete from dish where id in  
    <foreach collection="array" item="id" open="(" close=")" separator=",">  
        #{id}  
    </foreach>  
</delete>
```

```DishFlavorMapper
void deleteByDishIds(List<Long> ids);
```

```DishFlavorMapperXml
<delete id="deleteByDishIds">  
    delete from dish_flavor where dish_id in  
    <foreach collection="dishIds" item="dishId" separator="," open="(" close=")">  
        #{dishId}  
    </foreach>  
</delete>
```

```SetmealDishMapper.java
List<Long> getSetmealIdsByDishIds(List<Long> dishIds);
```

```SetmealDishMapperXml
<select id="getSetmealIdsByDishIds" resultType="java.lang.Long">  
    select setmeal_id from setmeal_dish where dish_id in  
    <foreach collection="dishIds" item="dishId" separator="," open="(" close=")">  
        #{dishId}  
    </foreach>  
</select>
```


---

## 修改菜品

1. 先进行获得菜品id的开发
```DishController
@GetMapping("/{id}")  
@ApiOperation("根据id查询菜品")  
public Result<DishVO> getById(@PathVariable Long id){  
    log.info("根据id查询菜品: {}", id);  
    DishVO dishVO = dishService.getByIdWithFlavor(id);  
    return Result.success(dishVO);  
}
```

```DishService
DishVO getByIdWithFlavor(Long id);
```

```DisiServiceImpl
public DishVO getByIdWithFlavor(Long id) {  
    //根据id查询菜品数据  
    Dish dish = dishMapper.getById(id);  
  
    //根据菜品id查询口味数据  
    List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);  
  
    //将查询的菜品数据组装成 DishVO    
    DishVO dishVO = new DishVO();  
    BeanUtils.copyProperties(dish,dishVO);  
    dishVO.setFlavors(dishFlavors);  
  
    return dishVO;  
}
```

```DishMapper.java
@Select("select * from dish where id = #{id}")  
Dish getById(Long id);
```

```DishFlavorMapper
@Select("select * from dish_flavor where dish_id = #{dishId}")  
List<DishFlavor> getByDishId(Long dishId);
```

---

2.进行修改菜品
```DishController
@PutMapping  
@ApiOperation("修改菜品")  
public Result update(@RequestBody DishDTO dishDTO){  
    log.info("修改菜品: {}", dishDTO);  
    dishService.updateWithFlavor(dishDTO);  
    return Result.success();  
}
```

```DishService
void updateWithFlavor(DishDTO dishDTO);
```

```DishServiceImpl
public void updateWithFlavor(DishDTO dishDTO) {  
    Dish dish = new Dish();  
    BeanUtils.copyProperties(dishDTO,dish);  
    //修改菜品表基本信息  
    dishMapper.update(dish);  
  
   //删除原有的口味数据 再插入新的口味数据  
    dishFlavorMapper.deleteByDishId(dishDTO.getId());  
  
    List<DishFlavor> flavors = dishDTO.getFlavors();  
    if(flavors != null && flavors.size() > 0){  
        flavors.forEach(dishFlavor -> {  
            dishFlavor.setDishId(dishDTO.getId());  
        });        //向口味表插入n条数据  
        dishFlavorMapper.insertBatch(flavors);  
    }}
```

```DishMapper
@AutoFill(value = OperationType.UPDATE)  
void update(Dish dish);
```

```DishMapperXml
<update id="update">  
    update dish  
    <set>  
        <if test="name != null">  
            name = #{name},  
        </if>  
        <if test="categoryId != null">  
            category_id = #{categoryId},  
        </if>  
        <if test="price != null">  
            price = #{price},  
        </if>  
        <if test="image != null">  
            image = #{image},  
        </if>  
        <if test="description != null">  
            description = #{description},  
        </if>  
        <if test="status != null">  
            status = #{status},  
        </if>  
        <if test="updateTime != null">  
            update_time = #{updateTime},  
        </if>  
        <if test="updateUser != null">  
            update_user = #{updateUser},  
        </if>  
    </set>  
        where id = #{id}  
  
</update>
```

