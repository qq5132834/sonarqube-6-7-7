package org.sonar.txt;

import org.sonar.api.Plugin;

public class TxtPlugin implements Plugin {

    @Override
    public void define(Plugin.Context context) {
        context.addExtension(TxtLanguage.class);
        context.addExtension(TxtProfileDefinition.class);
        context.addExtension(TxtSensor.class);
        context.addExtension(TxtContainRuleDefinition.class);
        context.addExtension(SayHelloFromScanner.class);

//        context.addExtensions(
//                TxtLanguage.class,
//                TxtProfileDefinition.class,
//                TxtSensor.class,
//                TxtContainRuleDefinition.class
//        );
    }

}
