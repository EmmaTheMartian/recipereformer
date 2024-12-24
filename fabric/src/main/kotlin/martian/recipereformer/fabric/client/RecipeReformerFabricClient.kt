package martian.recipereformer.fabric.client

import martian.recipereformer.fabric.RecipeReformerFabric.clientScriptPath
import martian.recipereformer.scripting.ScriptDispatcher
import net.fabricmc.api.ClientModInitializer

object RecipeReformerFabricClient : ClientModInitializer {
	override fun onInitializeClient() {
		ScriptDispatcher.runScriptsFromDirectory(clientScriptPath.toFile())
	}
}
