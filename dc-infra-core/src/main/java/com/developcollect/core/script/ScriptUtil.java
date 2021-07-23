package com.developcollect.core.script;

import cn.hutool.script.ScriptRuntimeException;

import javax.script.Invocable;
import javax.script.ScriptException;

public class ScriptUtil extends cn.hutool.script.ScriptUtil {

    /**
     * 执行JS脚本中的指定方法
     *
     * @param invocable js解析后的对象
     * @param func 方法名
     * @param args 方法参数
     * @return 结果
     */
    public static Object invokeFunction(Invocable invocable, String func, Object... args) {
        try {
            return invocable.invokeFunction(func, args);
        } catch (ScriptException | NoSuchMethodException e) {
            throw new ScriptRuntimeException(e);
        }
    }
}
