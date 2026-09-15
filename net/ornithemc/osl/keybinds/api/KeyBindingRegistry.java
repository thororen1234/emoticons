package net.ornithemc.osl.keybinds.api;

import net.minecraft.unmapped.C_23708450;

public interface KeyBindingRegistry {

	C_23708450 register(String name, int defaultKeyCode, String category);

	C_23708450 register(C_23708450 keybind);

}
