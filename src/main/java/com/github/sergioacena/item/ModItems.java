package com.github.sergioacena.item;

import java.util.function.Function;

import com.github.sergioacena.FireSwordMod;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;

/**
 * Aqui se registran todos los items del mod.
 *
 * Un "registro" (Registry) es la lista maestra que Minecraft consulta para saber
 * que cosas existen en el juego. Si un item no se mete en el registro durante el
 * arranque, para el juego simplemente no existe.
 */
public class ModItems {

	/**
	 * La espada de fuego.
	 *
	 * .sword(material, danyo, velocidad) es la forma de crear espadas en 1.21.11.
	 * Ojo: en versiones anteriores existia una clase SwordItem que habia que
	 * heredar; se elimino, asi que muchos tutoriales de internet estan obsoletos.
	 *
	 *  - ToolMaterial.DIAMOND -> hereda durabilidad, velocidad de minado y nivel
	 *    de encantamiento del diamante.
	 *  - 5.0F  -> danyo extra de la espada. OJO: NO es el danyo final. El juego
	 *    calcula: 1 (punyo del jugador) + 3 (bonus del material diamante) + 5 = 9.
	 *    Referencia vanilla: madera 4, piedra 5, hierro 6, diamante 7, netherita 8.
	 *    Con 5.0F la nuestra hace 9, o sea que pega mas fuerte que la netherita.
	 *  - -2.4F -> velocidad de ataque. Es negativa porque es una PENALIZACION al
	 *    cooldown: cuanto mas negativa, mas tarda en recargarse el golpe.
	 *  - .fireResistant() -> no se quema si cae en lava. Tematico y gratis.
	 */
	public static final Item FIRE_SWORD = register("fire_sword",
			properties -> new FireSwordItem(properties.sword(ToolMaterial.DIAMOND, 5.0F, -2.4F).fireResistant()));

	/**
	 * Crea un item y lo mete en el registro de Minecraft.
	 *
	 * @param name    nombre del item sin namespace (se le antepone el mod id)
	 * @param factory como construir el item a partir de sus propiedades
	 */
	private static Item register(String name, Function<Item.Properties, Item> factory) {
		// El "DNI" del item: namespace:ruta -> fire-sword-mod:fire_sword
		Identifier id = FireSwordMod.id(name);
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);

		// setId es OBLIGATORIO desde 1.21.2: el item debe conocer su propia clave
		// antes de construirse, o el juego revienta al arrancar.
		Item item = factory.apply(new Item.Properties().setId(key));

		return Registry.register(BuiltInRegistries.ITEM, key, item);
	}

	/**
	 * Cargar esta clase provoca que se ejecuten sus campos static, que es lo que
	 * dispara el registro. En Java los campos static solo se inicializan la primera
	 * vez que alguien toca la clase; si nadie nombra ModItems, la espada nunca se
	 * registraria. Por eso el inicializador del mod llama a este metodo vacio.
	 */
	public static void initialize() {
		// Anyade la espada a la pestanya de Combate del inventario de creativo.
		// Sin esto el item existe, pero solo se puede conseguir con /give.
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT)
				.register(entries -> entries.accept(FIRE_SWORD));
	}
}
