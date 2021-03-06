package exnihilofabrico.api.crafting

import alexiil.mc.lib.attributes.fluid.volume.FluidVolume
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import exnihilofabrico.util.asStack
import exnihilofabrico.util.getFluid
import exnihilofabrico.util.getId
import net.fabricmc.fabric.api.tag.TagRegistry
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.FluidBlock
import net.minecraft.fluid.Fluid
import net.minecraft.fluid.FluidState
import net.minecraft.fluid.Fluids
import net.minecraft.tag.Tag
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

class FluidIngredient(tags: MutableCollection<Tag<Fluid>> = mutableListOf(), matches: MutableSet<Fluid> = mutableSetOf()):
    AbstractIngredient<Fluid>(tags, matches) {

    constructor(vararg matches: Fluid): this(mutableListOf(), matches.toMutableSet())
    constructor(vararg matches: FluidVolume): this(mutableListOf<Tag<Fluid>>(), matches.map { it.rawFluid }.filterNotNull().toMutableSet())
    constructor(vararg tags: Tag<Fluid>): this(tags.toMutableList(), mutableSetOf())

    fun test(state: BlockState) = (state.block as? FluidBlock)?.let { test(it) } ?: false
    fun test(block: Block) = (block as? FluidBlock)?.let { test(it) } ?: false
    fun test(block: FluidBlock) = test(block.getFluid())
    fun test(state: FluidState) = test(state.fluid)
    fun test(stack: FluidVolume) = (stack.rawFluid)?.let { test(it) } ?: false

    fun flattenListOfBuckets() = flatten { it.bucketItem.asStack() }.filterNot { it.isEmpty }.toMutableList()

    override fun equals(other: Any?): Boolean {
        return (other as? FluidIngredient)?.let { other ->
            this.tags.size == other.tags.size &&
                    this.matches.size == other.matches.size &&
                    this.tags.containsAll(other.tags) &&
                    this.matches.containsAll(other.matches)
        }?: false
    }

    override fun hashCode(): Int {
        return tags.hashCode() xor matches.hashCode()
    }

    override fun serializeElement(t: Fluid, context: JsonSerializationContext) =
        JsonPrimitive(t.getId().toString())

    companion object {
        val EMPTY = FluidIngredient(Fluids.EMPTY)

        fun fromJson(json: JsonElement, context: JsonDeserializationContext) =
            fromJson(json,
                context,
                { deserializeTag(it, context) },
                { deserializeMatch(it, context) },
                { tags: MutableCollection<Tag<Fluid>>, matches: MutableSet<Fluid> ->
                    FluidIngredient(
                        tags,
                        matches
                    )
                })

        fun deserializeTag(json: JsonElement, context: JsonDeserializationContext): Tag<Fluid> =
            TagRegistry.fluid(Identifier(json.asString.split("#").last()))
        fun deserializeMatch(json: JsonElement, context: JsonDeserializationContext) =
            Registry.FLUID[(Identifier(json.asString))]
    }
}