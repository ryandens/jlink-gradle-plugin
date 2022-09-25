package com.ryandens.jlink

import org.gradle.api.internal.plugins.DefaultTemplateBasedStartScriptGenerator
import org.gradle.api.internal.plugins.StartScriptTemplateBindingFactory
import org.gradle.util.internal.TextUtil

class JlinkAwareStartScriptGenerator : DefaultTemplateBasedStartScriptGenerator(
  TextUtil.getUnixLineSeparator(),
  StartScriptTemplateBindingFactory.unix(),
  utf8ClassPathResource(JlinkAwareStartScriptGenerator::class.java, "unixStartScript.txt")
) {

}