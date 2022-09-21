package com.ryandens.jlink

import org.gradle.api.Transformer
import org.gradle.api.internal.plugins.UnixStartScriptGenerator
import org.gradle.jvm.application.scripts.JavaAppStartScriptGenerationDetails
import org.gradle.jvm.application.scripts.ScriptGenerator
import java.io.Writer

class JlinkAwareStartScriptGenerator(private val inner: ScriptGenerator = UnixStartScriptGenerator()) : ScriptGenerator by inner {


  class JlinkAwareTransformer(private val inner: Transformer<MutableMap<String, String>, JavaAppStartScriptGenerationDetails>) : Transformer<MutableMap<String, String>, JavaAppStartScriptGenerationDetails> {
    override fun transform(`in`: JavaAppStartScriptGenerationDetails): MutableMap<String, String> {
      TODO("Not yet implemented")
    }
  }

  private class JlinkAwareWriter(private val inner: Writer) : Writer() {

    override fun close() {
      inner.close()
    }

    override fun flush() {
      inner.flush()
    }

    override fun write(cbuf: CharArray, off: Int, len: Int) {
      inner.write(cbuf, off, len)
    }
  }
}