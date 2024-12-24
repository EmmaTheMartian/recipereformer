package martian.recipereformer.neoforge

import martian.recipereformer.RecipeReformer
import martian.recipereformer.getOrCreate
import martian.recipereformer.scripting.ScriptDispatcher
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.fml.loading.FMLPaths
import net.neoforged.neoforge.event.server.ServerLifecycleEvent
import net.neoforged.neoforge.event.server.ServerStartingEvent
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.runForDist

@Mod(RecipeReformer.MOD_ID)
object RecipeReformerNeoForge {
	val scriptPath = FMLPaths.GAMEDIR.get().resolve("rrscripts").getOrCreate()
	val commonScriptPath = scriptPath.resolve("common").getOrCreate()
	val serverScriptPath = scriptPath.resolve("server").getOrCreate()
	val clientScriptPath = scriptPath.resolve("client").getOrCreate()

	init {
		RecipeReformer.init()

		ScriptDispatcher.runScriptsFromDirectory(commonScriptPath.toFile())

		runForDist(
			clientTarget = {
				MOD_BUS.addListener(::onClientSetup)
			},
			serverTarget = {
			}
		)

		MOD_BUS.addListener(ServerStartingEvent::class.java) { event ->
			MOD_BUS.addListener(::onServerSetup)
		}
	}

	private fun onClientSetup(event: FMLClientSetupEvent) {
		ScriptDispatcher.runScriptsFromDirectory(clientScriptPath.toFile())
	}

	private fun onServerSetup(event: FMLClientSetupEvent) {
		ScriptDispatcher.runScriptsFromDirectory(serverScriptPath.toFile())
	}
}
