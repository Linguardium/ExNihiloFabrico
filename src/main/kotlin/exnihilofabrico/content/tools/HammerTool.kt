package exnihilofabrico.content.tools

import exnihilofabrico.ExNihiloFabrico
import exnihilofabrico.api.registry.ExNihiloRegistries
import net.minecraft.block.BlockState
import net.minecraft.item.Item
import net.minecraft.item.ToolItem
import net.minecraft.item.ToolMaterial

class HammerTool(material: ToolMaterial, settings: Item.Settings): ToolItem(material, settings.itemGroup(ExNihiloFabrico.ITEM_GROUP)) {
    override fun isEffectiveOn(state: BlockState): Boolean {
        return ExNihiloRegistries.HAMMER.isRegistered(state.block)
    }

}