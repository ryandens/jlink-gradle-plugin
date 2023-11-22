package com.ryandens.jlink

import org.gradle.api.internal.plugins.DefaultTemplateBasedStartScriptGenerator
import org.gradle.api.internal.plugins.StartScriptTemplateBindingFactory
import org.gradle.util.internal.TextUtil

/**
 * A custom [org.gradle.jvm.application.scripts.TemplateBasedScriptGenerator]
 * that replaces the %JAVA_EXE% with one that maps to the jlink JRE java binary.
 * Also see windowsStartScript.txt in src/main/resources
 */
class JlinkAwareWindowsStartScriptGenerator : DefaultTemplateBasedStartScriptGenerator(
    TextUtil.getWindowsLineSeparator(),
    StartScriptTemplateBindingFactory.windows(),
    utf8ClassPathResource(JlinkAwareWindowsStartScriptGenerator::class.java, "windowsStartScript.txt"),
)
