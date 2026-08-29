package com.github.sergioacena.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * La espada de fuego.
 *
 * Hasta ahora FIRE_SWORD era un Item normal y corriente: sus propiedades (danyo,
 * durabilidad, textura) bastaban. Pero para que ADEMAS haga algo al golpear
 * necesitamos codigo propio, y para eso creamos nuestra propia clase que hereda
 * de Item y sobrescribe uno de sus metodos.
 */
public class FireSwordItem extends Item {

	/** Cuantos segundos arde el enemigo tras el golpe. Toca este numero y prueba. */
	private static final float SEGUNDOS_DE_FUEGO = 5.0F;

	/** Cuantas particulas de llama salen en cada golpe. */
	private static final int NUM_PARTICULAS = 12;

	public FireSwordItem(Properties properties) {
		super(properties);
	}

	/**
	 * Minecraft llama a este metodo justo DESPUES de que la espada haga danyo.
	 *
	 * Existe tambien hurtEnemy(), que se ejecuta ANTES del danyo. Usamos la version
	 * "post" porque queremos reaccionar a un golpe que ya ha ocurrido de verdad.
	 *
	 * @param stack    la espada concreta que se ha usado (con su durabilidad, encantamientos...)
	 * @param target   quien recibe el golpe
	 * @param attacker quien lo da (normalmente el jugador)
	 */
	@Override
	public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		// Dejamos que Item haga primero lo suyo (gastar durabilidad, etc.).
		super.postHurtEnemy(stack, target, attacker);

		// 1) Prender fuego al enemigo. Es lo mismo que hace el encantamiento
		//    Aspecto Igneo, pero de serie y sin necesidad de encantar la espada.
		target.igniteForSeconds(SEGUNDOS_DE_FUEGO);

		// 2) Particulas de llama.
		//
		//    AQUI HAY UN CONCEPTO IMPORTANTE: cliente vs servidor.
		//    Minecraft ejecuta dos mundos a la vez. El SERVIDOR manda: decide la
		//    vida, el fuego, quien muere. El CLIENTE solo dibuja lo que el servidor
		//    le cuenta. Este metodo se ejecuta en AMBOS lados.
		//
		//    Si creasemos las particulas sin comprobar el lado, en una partida
		//    multijugador solo las veria quien da el golpe. Con sendParticles()
		//    desde el servidor, este se encarga de avisar a TODOS los jugadores
		//    cercanos. Por eso comprobamos que estamos en el servidor.
		if (target.level() instanceof ServerLevel serverLevel) {
			serverLevel.sendParticles(
					ParticleTypes.FLAME,
					target.getX(),                            // centro del enemigo
					target.getY() + target.getBbHeight() * 0.5, // a media altura
					target.getZ(),
					NUM_PARTICULAS,
					0.3, 0.4, 0.3,  // dispersion en X, Y, Z (que no salgan todas del mismo punto)
					0.02            // velocidad de las particulas
			);
		}
	}
}
