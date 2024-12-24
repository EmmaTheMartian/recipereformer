package martian.recipereformer.fabric

import martian.recipereformer.RecipeReformer
import martian.recipereformer.getOrCreate
import martian.recipereformer.scripting.ScriptDispatcher
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.loader.api.FabricLoader

object RecipeReformerFabric : ModInitializer {
	val scriptPath = FabricLoader.getInstance().gameDir.resolve("rrscripts").getOrCreate()
	val commonScriptPath = scriptPath.resolve("common").getOrCreate()
	val serverScriptPath = scriptPath.resolve("server").getOrCreate()
	val clientScriptPath = scriptPath.resolve("client").getOrCreate()

	override fun onInitialize() {
		RecipeReformer.init()

		ScriptDispatcher.runScriptsFromDirectory(commonScriptPath.toFile())

		ServerLifecycleEvents.SERVER_STARTING.register { server ->
			ScriptDispatcher.runScriptsFromDirectory(serverScriptPath.toFile())
		}
	}
}
