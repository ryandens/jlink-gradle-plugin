package com.ryandens.jlink

import org.gradle.api.internal.plugins.DefaultTemplateBasedStartScriptGenerator
import org.gradle.api.internal.plugins.StartScriptTemplateBindingFactory
import org.gradle.util.internal.TextUtil

/**
 * A custom [org.gradle.jvm.application.scripts.TemplateBasedScriptGenerator]
 * that changes the default run behavior so that it maps to the jlink JRE java binary.
 * Also see unixStartScript.txt in src/main/resources
 * The original source of unixStartScript.txt can be found here:
 * https://github.com/gradle/gradle/blob/master/subprojects/plugins/src/main/resources/org/gradle/api/internal/plugins/unixStartScript.txt
 */
class JlinkAwareUnixStartScriptGenerator : DefaultTemplateBasedStartScriptGenerator(
    TextUtil.getUnixLineSeparator(),
    StartScriptTemplateBindingFactory.unix(),
    utf8ClassPathResource(JlinkAwareUnixStartScriptGenerator::class.java, "unixStartScript.txt"),
)
