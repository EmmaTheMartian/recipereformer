package martian.recipereformer.scripting

import martian.recipereformer.scripting.host.ScriptHost
import java.io.File

object ScriptDispatcher {
	fun runScriptsFromDirectory(directory: File) {
		directory.walkTopDown().forEach { file ->
			ScriptHost.evalFile(file)
		}
	}
}
