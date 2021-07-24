package org.sutopia.starsector.mod.phaseprototype.hullmod;

import com.fs.starfarer.api.combat.FluxTrackerAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;

public class PrototypePhaseShunt extends PhasePrototypeBase {
	
	private static final String SHUNT_SOURCE = "su_phase_flux_shunt";
	private static final float EVIL_NUMBER = 0.001f;
	
	public void advanceInCombat(ShipAPI ship, float amount) {
		if (ship == null || !ship.isAlive()) {
			return;
		}
		MutableShipStatsAPI stats = ship.getMutableStats();
		FluxTrackerAPI fluxStat = ship.getFluxTracker();
		
		if (ship.isPhased() || fluxStat.isOverloadedOrVenting()) {
			stats.getFluxDissipation().unmodify(SHUNT_SOURCE);
		} else {
			stats.getFluxDissipation().modifyMult(SHUNT_SOURCE, EVIL_NUMBER);
			float dissipation = amount * stats.getFluxDissipation().getModifiedValue() / EVIL_NUMBER;
			
			float hard = fluxStat.getHardFlux();
			float soft = fluxStat.getCurrFlux() - hard;
			
			if (hard >= dissipation) {
				fluxStat.setHardFlux(hard - dissipation);
				fluxStat.setCurrFlux(soft + hard - dissipation);
				return;
			}
			
			dissipation -= hard;
			fluxStat.setHardFlux(0f);
			
			if (soft >= dissipation) {
				fluxStat.setCurrFlux(soft - dissipation);
			} else {
				fluxStat.setCurrFlux(0f);
			}
		}
	}
}
