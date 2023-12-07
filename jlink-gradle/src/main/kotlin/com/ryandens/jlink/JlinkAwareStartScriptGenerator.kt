package com.ryandens.jlink

import org.gradle.api.internal.plugins.DefaultTemplateBasedStartScriptGenerator
import org.gradle.api.internal.plugins.StartScriptTemplateBindingFactory

class JlinkAwareStartScriptGenerator(
    lineSeparator: String,
    bindingFactory: StartScriptTemplateBindingFactory,
    startScriptTemplateFileName: String,
) : DefaultTemplateBasedStartScriptGenerator(
        lineSeparator,
        bindingFactory,
        utf8ClassPathResource(JlinkAwareStartScriptGenerator::class.java, startScriptTemplateFileName),
)
