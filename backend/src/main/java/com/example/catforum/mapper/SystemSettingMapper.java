package com.example.catforum.mapper;

import com.example.catforum.entity.SystemSetting;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 系统设置数据访问接口
 * 提供系统设置表的CRUD操作
 */
@Mapper
public interface SystemSettingMapper {

    /**
     * 根据键名查询设置项
     * @param key 设置项键名
     * @return 设置项
     */
    @Select("SELECT * FROM system_setting WHERE setting_key = #{key}")
    SystemSetting findByKey(@Param("key") String key);

    /**
     * 查询所有设置项
     * @return 设置项列表
     */
    @Select("SELECT * FROM system_setting ORDER BY id")
    List<SystemSetting> findAll();

    /**
     * 插入新设置项
     * @param setting 设置项实体
     * @return 影响的行数
     */
    @Insert("INSERT INTO system_setting (setting_key, setting_value, description, create_time, update_time) " +
            "VALUES (#{settingKey}, #{settingValue}, #{description}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SystemSetting setting);

    /**
     * 更新设置项
     * @param setting 设置项实体
     * @return 影响的行数
     */
    @Update("UPDATE system_setting SET setting_value = #{settingValue}, description = #{description}, update_time = NOW() " +
            "WHERE setting_key = #{settingKey}")
    int update(SystemSetting setting);

    /**
     * 根据键名更新设置值
     * @param key 键名
     * @param value 值
     * @return 影响的行数
     */
    @Update("UPDATE system_setting SET setting_value = #{value}, update_time = NOW() WHERE setting_key = #{key}")
    int updateValue(@Param("key") String key, @Param("value") String value);

    /**
     * 根据键名删除设置项
     * @param key 键名
     * @return 影响的行数
     */
    @Delete("DELETE FROM system_setting WHERE setting_key = #{key}")
    int deleteByKey(@Param("key") String key);

    /**
     * 根据ID删除设置项
     * @param id ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM system_setting WHERE id = #{id}")
    int deleteById(@Param("id") Integer id);
}
