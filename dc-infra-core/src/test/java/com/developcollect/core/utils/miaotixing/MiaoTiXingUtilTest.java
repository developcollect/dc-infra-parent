package com.developcollect.core.utils.miaotixing;

import org.junit.Test;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import java.util.List;

import static org.junit.Assert.*;

public class MiaoTiXingUtilTest {


    public static void main(String[] args) {
        MiaoTriggerRequest request = new MiaoTriggerRequest();
        request.setId("tDmTGmP");
        request.setOption("nosms");
        MiaoTriggerResponse response = MiaoTiXingUtil.trigger(request);
        System.out.println(response);
    }


    @Test
    public void tt() {
        ScriptEngineManager manager = new ScriptEngineManager();

        List<ScriptEngineFactory> factories = manager.getEngineFactories();

        for (ScriptEngineFactory f : factories) {
            System.out.println(
                    "egine name:" + f.getEngineName() +
                            ",engine version:" + f.getEngineVersion() +
                            ",language name:" + f.getLanguageName() +
                            ",language version:" + f.getLanguageVersion() +
                            ",names:" + f.getNames() +
                            ",mime:" + f.getMimeTypes() +
                            ",extension:" + f.getExtensions());
        }

    }



    @Test
    public void tt3() {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        Bindings bindings = engine.createBindings();

    }
}