package com.example.service;

import com.example.exception.UnavailableNameException;

/**
 * 所给实体应当包含这样的属性， 它指示当前实体的名称， 并且名称具有唯一性
 * 即所给属性对应在表中的字段含有唯一约束
 * @author Lexin Huang
 */
public interface UniqueNameQueryService<T> {

    /**
     * 根据实体在表中含有唯一约束的用于指示名称的属性, 查找对应实体
     * @param name 含有唯一约束的用于指示名称的属性值
     * @return 实体对象 (如果存在的话), 否则为 null
     */
    T getByUniqueName(String name);

    /**
     * 判断所给的实体名称是否存在
     * @param name 实体的名称
     * @return true 当所给 name 存在时, 否则为 false
     */
    default boolean exist(String name) {
        return getByUniqueName(name) != null;
    }

    /**
     * 查看所给名称是否可用, 否则抛出异常
     * @param name 所给的实体名称
     * @throws UnavailableNameException 当对应的名称已经存在时
     */
    void checkIfNameIsAvailable(String name) throws UnavailableNameException;

}
