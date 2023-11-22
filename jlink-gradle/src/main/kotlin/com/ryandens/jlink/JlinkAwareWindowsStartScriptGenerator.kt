package com.ryandens.jlink

import org.gradle.api.internal.plugins.DefaultTemplateBasedStartScriptGenerator
import org.gradle.api.internal.plugins.StartScriptTemplateBindingFactory
import org.gradle.util.internal.TextUtil

class JlinkAwareWindowsStartScriptGenerator : DefaultTemplateBasedStartScriptGenerator(
    TextUtil.getWindowsLineSeparator(),
    StartScriptTemplateBindingFactory.windows(),
    utf8ClassPathResource(JlinkAwareWindowsStartScriptGenerator::class.java, "windowsStartScript.txt"),
)
