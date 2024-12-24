package martian.recipereformer.scripting.scriptdef

import kotlinx.coroutines.runBlocking
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.api.ScriptCollectedData
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.ScriptConfigurationRefinementContext
import kotlin.script.experimental.api.asSuccess
import kotlin.script.experimental.api.collectedAnnotations
import kotlin.script.experimental.api.defaultImports
import kotlin.script.experimental.api.dependencies
import kotlin.script.experimental.api.onSuccess
import kotlin.script.experimental.api.refineConfiguration
import kotlin.script.experimental.api.with
import kotlin.script.experimental.dependencies.CompoundDependenciesResolver
import kotlin.script.experimental.dependencies.DependsOn
import kotlin.script.experimental.dependencies.FileSystemDependenciesResolver
import kotlin.script.experimental.dependencies.Repository
import kotlin.script.experimental.dependencies.maven.MavenDependenciesResolver
import kotlin.script.experimental.dependencies.resolveFromScriptSourceAnnotations
import kotlin.script.experimental.jvm.JvmDependency
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm

@KotlinScript(
	fileExtension = "rr.kts",
	compilationConfiguration = ScriptWithMinecraftConfiguration::class
)
abstract class ScriptDef

object ScriptWithMinecraftConfiguration : ScriptCompilationConfiguration({
	defaultImports(DependsOn::class, Repository::class)
	jvm {
		// Extract the entire classpath for usage in the script
		dependenciesFromCurrentContext(wholeClasspath = true)
	}
	// Callbacks
	refineConfiguration {
		// Process specified annotations with the provided handler
		onAnnotations(DependsOn::class, Repository::class, handler = ScriptWithMinecraftConfiguration::configureMavenDepsOnAnnotations)
	}
}) {
	private fun readResolve(): Any = ScriptWithMinecraftConfiguration

	fun configureMavenDepsOnAnnotations(context: ScriptConfigurationRefinementContext): ResultWithDiagnostics<ScriptCompilationConfiguration> {
		val annotations = context.collectedData?.get(ScriptCollectedData.collectedAnnotations)?.takeIf { it.isNotEmpty() }
			?: return context.compilationConfiguration.asSuccess()
		return runBlocking {
			resolver.resolveFromScriptSourceAnnotations(annotations)
		}.onSuccess {
			context.compilationConfiguration.with {
				dependencies.append(JvmDependency(it))
			}.asSuccess()
		}
	}

	private val resolver = CompoundDependenciesResolver(FileSystemDependenciesResolver(), MavenDependenciesResolver())
}
