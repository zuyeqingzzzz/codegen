package com.zyq.apt.processor;


import com.squareup.javapoet.MethodSpec;
import com.zyq.common.constant.MethodEnum;

import java.util.*;
import java.util.stream.Collectors;

public interface MethodCreator {

    Optional<MethodSpec> list();

    Optional<MethodSpec> insert();

    Optional<MethodSpec> update();

    Optional<MethodSpec> delete();

    Optional<MethodSpec> findById();

    Optional<MethodSpec> findByPage();

    Optional<MethodSpec> voList();


    default List<MethodSpec> dispatch(MethodEnum[] methodEnum) {

        if (Arrays.asList(methodEnum).contains(MethodEnum.NONE)) {
            return new ArrayList<>();
        }

        Map<MethodEnum, Optional<MethodSpec>> methodMap = new HashMap<>();
        methodMap.put(MethodEnum.LIST, list());
        methodMap.put(MethodEnum.EDIT, update());
        methodMap.put(MethodEnum.INSERT, insert());
        methodMap.put(MethodEnum.DELETE, delete());
        methodMap.put(MethodEnum.VOLIST, voList());
        methodMap.put(MethodEnum.FINDBYID, findById());
        methodMap.put(MethodEnum.FINDBYPAGE, findByPage());

        return Arrays.stream(methodEnum)
                .map(methodMap::get)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

    }

}
