package com.wenkrang.famara.command;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Pattern;

/**
 * 命令参数抽象类，定义了命令行参数的基本结构和验证方法
 */
public abstract class CmdArgs {
    /**
     * 获取参数的描述信息
     * 
     * @return 参数描述列表
     */
    public abstract List<String> tabDescribe();
    
    /**
     * 测试给定值是否符合参数要求
     * 
     * @param value 待测试的参数值
     * @return 如果值符合参数要求则返回true，否则返回false
     */
    public abstract boolean test(@NotNull String value);

    /**
     * 字符串类型参数，支持可空设置
     */
    public static class StringArgument extends CmdArgs {
        private final String description;
        final boolean nullable;

        /**
         * 构造一个字符串参数
         * 
         * @param description 参数描述信息
         * @param nullable 是否允许为空
         */
        public StringArgument(String description, boolean nullable) {
            this.description = description;
            this.nullable = nullable;
        }
        
        @Override
        public List<String> tabDescribe() {
            return List.of('<' + description + '>' + (nullable ? "?" : ""));
        }

        @Override
        public boolean test(@NotNull String value) {
            return nullable || !value.isEmpty();
        }
    }

    /**
     * 整数类型参数，继承自字符串参数
     */
    public static final class IntArgument extends StringArgument {

        /**
         * 构造一个整数参数
         * 
         * @param description 参数描述信息
         * @param nullable 是否允许为空
         */
        public IntArgument(String description, boolean nullable) {
            super(description, nullable);
        }

        @Override
        public boolean test(@NotNull String value) {
            //使用正则表达式检测值是否为数字
            return Pattern.compile("[0-9]+").matcher(value).matches() ||
                    //如果可空也返回合法
                    (this.nullable && value.isEmpty());
        }
    }

    /**
     * 固定值参数，只允许特定的值
     */
    public static class FixedArgument extends CmdArgs {
        //可用的固定参数
        private final List<String> applicable;
        final boolean nullable;

        /**
         * 构造一个固定值参数
         * 
         * @param applicable 允许的值列表
         * @param nullable 是否允许为空
         */
        public FixedArgument(List<String> applicable, boolean nullable) {
            this.applicable = applicable;
            this.nullable = nullable;
        }

        @Override
        public List<String> tabDescribe() {
            return applicable;
        }

        @Override
        public boolean test(@NotNull String value) {
            return applicable.contains(value) || (nullable && value.isEmpty());
        }
    }

    /**
     * 弱固定值参数，继承自固定值参数
     */
    public static final class WeakFixedArgument extends FixedArgument {
        /**
         * 构造一个弱固定值参数
         * 值既可以是补全列表里面的，也可以是自定义的
         * 
         * @param applicable 允许的值列表
         * @param nullable 是否允许为空
         */
        public WeakFixedArgument(List<String> applicable, boolean nullable) {
            super(applicable, nullable);
        }

        @Override
        public boolean test(@NotNull String value) {
            return nullable || !value.isEmpty();
        }
    }
}
