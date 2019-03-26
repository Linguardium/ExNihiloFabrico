package exnihilocreatio.recipes.defaults

import exnihilocreatio.texturing.Color
import exnihilocreatio.util.ItemInfo
import exnihilocreatio.util.getOreDictEntry

enum class EnumModdedMetals(val color: Color) {
    ALUMINUM(Color("BFBFBF")),
    COPPER(Color("FF9933")),
    LEAD(Color("330066")),
    NICKEL(Color("FFFFCC")),
    SILVER(Color("F2F2F2")),
    TIN(Color("E6FFF2")),
    TUNGSTEN(Color("1C1C1C")),
    URANIUM(Color("4E5B43")),
    ZINC(Color("A59C74"));

    fun getRegistryName(): String {
        return name.toLowerCase()
    }

    private fun getConcatableName(): String {
        return getRegistryName().capitalize()
    }

    fun getOreName(): String {
        return "ore" + getConcatableName()
    }

    fun getIngotName(): String {
        return "ingot" + getConcatableName()
    }

    /**
     * Attempt to grab the first ingot from the ore dictionary to be the registered ingot
     */
    fun getIngot(): ItemInfo? {
        return getOreDictEntry(getIngotName())
    }


}