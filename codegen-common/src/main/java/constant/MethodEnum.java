package constant;

import java.util.Arrays;

/**
 * @author YiQing
 */
public enum MethodEnum {

    /**
     * 需要生成的方法
     */
    INSERT,
    EDIT,
    DELETE,
    INFO,
    LIST,
    VOLIST,
    FINDBYID,
    EXPORT,
    FINDBYPAGE,
    ;



    public static boolean hasMethod(MethodEnum target, MethodEnum[] source) {
        return Arrays.asList(source).contains(target);
    }

}
