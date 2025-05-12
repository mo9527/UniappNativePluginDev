package com.wanyi.plugins.utils.text;


import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 * 
 * @author wanyi
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils
{

    /** 下划线 */
    private static final char SEPARATOR = '_';

    /**
     * * 判断一个字符串是否为空串
     * 
     * @param str String
     * @return true：为空 false：非空
     */
    public static boolean isEmpty(String str)
    {

        return isNull(str) || equals(EMPTY, trimToEmpty(str));
    }

    /**
     * * 任何一个字符串是否为空串
     *
     * @param strs String
     * @return true：为空 false：非空
     */
    public static boolean anyEmpty(String... strs)
    {
        if (ArrayUtils.isEmpty(strs)) return true;
        return Arrays.stream(strs).anyMatch(StringUtils::isEmpty);
    }

    /**
     * * 判断一个字符串是否为非空串
     * 
     * @param str String
     * @return true：非空串 false：空串
     */
    public static boolean isNotEmpty(String str)
    {
        return !isEmpty(str);
    }

    /**
     * * 判断一个对象是否为空
     * 
     * @param object Object
     * @return true：为空 false：非空
     */
    public static boolean isNull(Object object)
    {
        return object == null;
    }

    /**
     * * 判断一个对象是否非空
     * 
     * @param object Object
     * @return true：非空 false：空
     */
    public static boolean isNotNull(Object object)
    {
        return !isNull(object);
    }

    /**
     * 截取字符串
     * 
     * @param str 字符串
     * @param start 开始
     * @return 结果
     */
    public static String substring(final String str, int start)
    {
        if (str == null)
        {
            return EMPTY;
        }

        if (start < 0)
        {
            start = str.length() + start;
        }

        if (start < 0)
        {
            start = 0;
        }
        if (start > str.length())
        {
            return EMPTY;
        }

        return str.substring(start);
    }

    /**
     * 截取字符串
     * 
     * @param str 字符串
     * @param start 开始
     * @param end 结束
     * @return 结果
     */
    public static String substring(final String str, int start, int end)
    {
        if (str == null)
        {
            return EMPTY;
        }

        if (end < 0)
        {
            end = str.length() + end;
        }
        if (start < 0)
        {
            start = str.length() + start;
        }

        if (end > str.length())
        {
            end = str.length();
        }

        if (start > end)
        {
            return EMPTY;
        }

        if (start < 0)
        {
            start = 0;
        }
        if (end < 0)
        {
            end = 0;
        }

        return str.substring(start, end);
    }

    /**
     * 判断是否为空，并且不是空白字符
     * 
     * @param str 要判断的value
     * @return 结果
     */
    public static boolean hasText(String str)
    {
        return isNotNull(str) && isNotEmpty(str) && containsText(str);
    }

    private static boolean containsText(CharSequence str)
    {
        int strLen = str.length();
        for (int i = 0; i < strLen; i++)
        {
            if (!Character.isWhitespace(str.charAt(i)))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 格式化文本, {} 表示占位符<br>
     * 此方法只是简单将占位符 {} 按照顺序替换为参数<br>
     * 如果想输出 {} 使用 \\转义 { 即可，如果想输出 {} 之前的 \ 使用双转义符 \\\\ 即可<br>
     * 例：<br>
     * 通常使用：format("this is {} for {}", "a", "b") -> this is a for b<br>
     * 转义{}： format("this is \\{} for {}", "a", "b") -> this is \{} for a<br>
     * 转义\： format("this is \\\\{} for {}", "a", "b") -> this is \a for b<br>
     * 
     * @param template 文本模板，被替换的部分用 {} 表示
     * @param params 参数值
     * @return 格式化后的文本
     */
    public static String format(String template, Object... params)
    {
        if (ArrayUtils.isEmpty(params) || isEmpty(template))
        {
            return template;
        }
        return StrFormatter.format(template, params);
    }


    /**
     * Format parameters to a string using the specified pattern.
     * usage:
     *   String template = "Hello, {userName}";
     *   Map<String, Object> params = new HashMap<String, Object>(){{
     *       put("userName", "wanyi");
     *   }};
     *   String format = StringUtils.format(template, params);
     *   System.out.println(format);
     *   The output string: Hello, wanyi
     * @param strPattern The pattern string
     * @param params    The params map
     * @return  String
     */
    public static String format(String strPattern, Map<String, Object> params){
        if (isEmpty(strPattern) || MapUtils.isEmpty(params))
        {
            return strPattern;
        }
        return StrFormatter.format(strPattern, params);
    }

    /**
     * @param template {0}您好,欢迎来到{1}, 记住我们的网址是:{2}
     * @param objects new Object[]{"医生", "医助宝", "https://www.wanyih.com/"}
     * @return 医生您好,欢迎来到 医助宝, 记住我们的网址是: https://www.wanyih.com/
     */
    public static String formatNumberic(String template,Object...objects){
        if (isEmpty(template) || ArrayUtils.isEmpty(objects))
        {
            return template;
        }
        return MessageFormat.format(template, objects);
    }

    /**
     * 是否为http(s)://开头
     * 
     * @param link 链接
     * @return 结果
     */
    public static boolean isHTTP(String link)
    {
        return startsWithAny(link, "http", "https");
    }

    /**
     * 驼峰转下划线命名
     */
    public static String toUnderScoreCase(String str)
    {
        if (str == null)
        {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        // 前置字符是否大写
        boolean preCharIsUpperCase;
        // 当前字符是否大写
        boolean curreCharIsUpperCase;
        // 下一字符是否大写
        boolean nexteCharIsUpperCase = true;
        for (int i = 0; i < str.length(); i++)
        {
            char c = str.charAt(i);
            if (i > 0)
            {
                preCharIsUpperCase = Character.isUpperCase(str.charAt(i - 1));
            }
            else
            {
                preCharIsUpperCase = false;
            }

            curreCharIsUpperCase = Character.isUpperCase(c);

            if (i < (str.length() - 1))
            {
                nexteCharIsUpperCase = Character.isUpperCase(str.charAt(i + 1));
            }

            if (preCharIsUpperCase && curreCharIsUpperCase && !nexteCharIsUpperCase)
            {
                sb.append(SEPARATOR);
            }
            else if ((i != 0 && !preCharIsUpperCase) && curreCharIsUpperCase)
            {
                sb.append(SEPARATOR);
            }
            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }

    /**
     * 是否包含字符串
     * 
     * @param str 验证字符串
     * @param strs 字符串组
     * @return 包含返回true
     */
    public static boolean inStringIgnoreCase(String str, String... strs)
    {
        if (str != null && strs != null)
        {
            for (String s : strs)
            {
                if (str.equalsIgnoreCase(trimToEmpty(s)))
                {
                    return true;
                }
            }
        }
        return false;
    }

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
     * 驼峰式命名法 例如：user_name->userName
     */
    public static String toCamelCase(String s)
    {
        if (s == null)
        {
            return null;
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

    /**
     * 查找指定字符串是否匹配指定字符串列表中的任意一个字符串
     * 
     * @param str 指定字符串
     * @param strs 需要检查的字符串数组
     * @return 是否匹配
     */
    public static boolean matches(String str, List<String> strs)
    {
        if (strs == null || strs.isEmpty())
        {
            return false;
        }
        for (String pattern : strs)
        {
            if (isMatch(pattern, str))
            {
                return true;
            }
        }
        return false;
    }

    private static boolean isMatch(String pattern, String str) {
        if (StringUtils.isEmpty(str) || StringUtils.isEmpty(pattern)) {
            return false;
        }
        //更具pattern正则表达式匹配字符串str
        return Pattern.matches(pattern, str);
    }


    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj)
    {
        return (T) obj;
    }

    /**
     * 移除str前后的指定remove
     */
    public static String removeTrimIgnoreCase(String str, String... removes){
        if (ArrayUtils.isEmpty(removes)) return str;

        for (String remove: removes) {
            str = removeStartIgnoreCase(str, remove);
            str = removeEndIgnoreCase(str, remove);
        }

        return str;
    }

    /**
     * 移除str前后所有的指定remove
     */
    public static String removeAllTrimIgnoreCase(String str, String... removes){
        if (ArrayUtils.isEmpty(removes)) return str;

        for (String remove: removes) {
            while (startsWithIgnoreCase(str, remove)) {
                str = removeStartIgnoreCase(str, remove);
            }
            while (endsWith(str, remove)) {
                str = removeEndIgnoreCase(str, remove);
            }
        }

        return str;
    }

    public static List<String> partition(String str,int limitSize){
        if (StringUtils.isEmpty(str)) return null;
        List<String> resList = new ArrayList<>();
        if (str.length() <= limitSize){
            resList.add(str);
            return resList;
        }

        int len = str.length();
        int splitCount = (len+ limitSize -1)/limitSize;
        for (int idx = 0 ; idx < splitCount;idx ++){
            int startIdx = idx * limitSize;
            int endIdx = Math.min((idx + 1) * limitSize, len);
            String tempStr = str.substring(startIdx, endIdx);
            resList.add(tempStr);
        }

        return resList;
    }


}