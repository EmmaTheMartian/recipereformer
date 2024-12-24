package martian.recipereformer.scripting.host

import martian.recipereformer.scripting.scriptdef.ScriptDef
import java.io.File
import kotlin.script.experimental.api.EvaluationResult
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate

object ScriptHost {
	fun evalFile(scriptFile: File): ResultWithDiagnostics<EvaluationResult> {
		val compilationConfiguration = createJvmCompilationConfigurationFromTemplate<ScriptDef>()
		return BasicJvmScriptingHost().eval(scriptFile.toScriptSource(), compilationConfiguration, null)
	}
}
