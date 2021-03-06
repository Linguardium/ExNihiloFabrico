package exnihilofabrico.registry.barrel

import com.google.gson.reflect.TypeToken
import exnihilofabrico.ExNihiloFabrico
import exnihilofabrico.api.recipes.barrel.CompostRecipe
import exnihilofabrico.api.registry.ICompostRegistry
import exnihilofabrico.compatibility.modules.MetaModule
import exnihilofabrico.compatibility.rei.barrel.CompostCategory
import exnihilofabrico.compatibility.rei.barrel.REICompostRecipe
import exnihilofabrico.registry.AbstractRegistry
import exnihilofabrico.util.asStack
import net.minecraft.item.ItemStack
import java.io.File
import java.io.FileReader
import java.lang.reflect.Type
import kotlin.math.ceil

data class CompostRegistry(val registry: MutableList<CompostRecipe> = mutableListOf()):
    AbstractRegistry<MutableList<CompostRecipe>>(), ICompostRegistry {

    override fun serializable() = registry
    override fun clear() = registry.clear()

    override fun register(recipe: CompostRecipe): Boolean {
        val conflict = registry.any { it.ingredient == recipe.ingredient }

        if(conflict) {
            ExNihiloFabrico.LOGGER.warn("Conflicting Compost Recipe not registered: $recipe")
            return false
        }

        return registry.add(recipe)
    }

    override fun registerJson(file: File) {
        if(file.exists()){
            val json: MutableList<CompostRecipe> = gson.fromJson(
                FileReader(file),
                SERIALIZATION_TYPE
            )
            json.forEach { register(it) }
        }
    }
    override fun getRecipe(stack: ItemStack) = registry.firstOrNull { it.test(stack) }

    override fun getREIRecipes(): Collection<REICompostRecipe> {
        return registry.map { it.result.item }.toSet().map {output ->
            registry
                    // Grab all the same outputs
                .filter { it.result.item == output }
                    // Get a list of ALL possible inputs
                .map {recipe ->
                    recipe.ingredient.flatten().map {
                        it.asStack(ceil((1.0 / recipe.amount)).toInt())
                    }
                }.flatten()
                    // Chunk the list up based on max number of inputs
                .chunked(CompostCategory.MAX_INPUT) {inputs ->
                    REICompostRecipe(inputs.toList(), output.asStack())
                }
        }.flatten()
    }

    companion object {
        val SERIALIZATION_TYPE: Type = object : TypeToken<MutableList<CompostRecipe>>() {}.type
        fun fromJson(file: File) = fromJson(
            file,
            { CompostRegistry() },
            MetaModule::registerCompost
        )
    }

}