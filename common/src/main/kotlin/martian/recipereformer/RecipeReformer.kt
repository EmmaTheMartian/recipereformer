package martian.recipereformer

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

object RecipeReformer {
	const val MOD_ID: String = "recipereformer"

	fun init() {
		println("recipe reformer :3")
	}
}

fun Path.getOrCreate(): Path {
	if (!Files.exists(this)) {
		try {
			Files.createDirectories(this)
		} catch (exception: IOException) {
			throw RuntimeException(exception)
		}
	}
	return this
}
