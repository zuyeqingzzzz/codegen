package utils;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public final class StringUtils extends org.apache.commons.lang3.StringUtils{


    final static Pattern COMPILE = Pattern.compile("([\\u4e00-\\u9fa5]+)");

    private StringUtils() {
    }

    // 首字母转小写
    public static String camel(String source) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, source);
    }



    public static boolean containsNull(String... list) {
        List<String> temp = Lists.newArrayList();
        Collections.addAll(temp, list);
        List<String> nullList = temp.stream().filter(Objects::isNull).collect(Collectors.toList());
        return nullList.size() > 0;
    }


    public static String cleanTableComment(String tableComment) {
        if (tableComment.length() == 0) {
            return "";
        }
      return tableComment.endsWith("表") ? tableComment.substring(0, tableComment.length() - 1) : tableComment;
    }

    public static String cleanFieldComment(String fieldComment) {
        Matcher matcher = COMPILE.matcher(fieldComment);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return fieldComment;
    }


    private static final char SEPARATOR = '_';


    /**
     * 将下划线大写方式命名的字符串转换为驼峰式。如果转换前的下划线大写方式命名的字符串为空，则返回空字符串。 例如：HELLO_WORLD->HelloWorld
     *
     * @param name 转换前的下划线大写方式命名的字符串
     * @return 转换后的驼峰式命名的字符串
     */
    public static String convertToCamelCase(String name)
    {
        StringBuilder result = new StringBuilder();
        // 快速检查
        if (name == null || name.isEmpty())
        {
            // 没必要转换
            return "";
        }
        else if (!name.contains("_"))
        {
            // 不含下划线，仅将首字母大写
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        // 用下划线将原始字符串分割
        String[] camels = name.split("_");
        for (String camel : camels)
        {
            // 跳过原始字符串中开头、结尾的下换线或双重下划线
            if (camel.isEmpty())
            {
                continue;
            }
            // 首字母大写
            result.append(camel.substring(0, 1).toUpperCase());
            result.append(camel.substring(1).toLowerCase());
        }
        return result.toString();
    }

    /**
     * 驼峰式命名法
     * 例如：user_name->userName
     */
    public static String toCamelCase(String s)
    {
        if (s == null)
        {
            return null;
        }
        if (s.indexOf(SEPARATOR) == -1)
        {
            return s;
        }
        s = s.toLowerCase();
        StringBuilder sb = new StringBuilder(s.length());
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);

            if (c == SEPARATOR)
            {
                upperCase = true;
            }
            else if (upperCase)
            {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            }
            else
            {
                sb.append(c);
            }
        }
        return sb.toString();
    }

}
