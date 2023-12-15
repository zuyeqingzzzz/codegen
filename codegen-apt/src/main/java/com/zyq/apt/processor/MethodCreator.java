package com.zyq.apt.processor;


import com.squareup.javapoet.MethodSpec;
import com.zyq.common.constant.MethodEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface MethodCreator {

    Optional<MethodSpec> list();

    Optional<MethodSpec> insert();

    Optional<MethodSpec> update();

    Optional<MethodSpec> delete();

    Optional<MethodSpec> findById();

    Optional<MethodSpec> findByPage();

    Optional<MethodSpec> voList();


    default List<MethodSpec> dispatch(MethodEnum[] methodEnum) {
        List<MethodSpec> methodSpecs = new ArrayList<>();
        List<MethodEnum> methodEnums = Arrays.asList(methodEnum);

        if (methodEnums.contains(MethodEnum.LIST)) {
            this.list().ifPresent(methodSpecs::add);
        }

        if (methodEnums.contains(MethodEnum.INSERT)) {
            this.insert().ifPresent(methodSpecs::add);
        }

        if (methodEnums.contains(MethodEnum.DELETE)) {
            this.delete().ifPresent(methodSpecs::add);
        }

        if (methodEnums.contains(MethodEnum.EDIT)) {
            this.update().ifPresent(methodSpecs::add);
        }

        if (methodEnums.contains(MethodEnum.FINDBYID)) {
            this.findById().ifPresent(methodSpecs::add);
        }

        if (methodEnums.contains(MethodEnum.VOLIST)) {
            this.voList().ifPresent(methodSpecs::add);
        }

        if (methodEnums.contains(MethodEnum.FINDBYPAGE)) {
            this.findByPage().ifPresent(methodSpecs::add);
        }


        return methodSpecs;
    }

}
