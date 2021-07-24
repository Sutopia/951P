package org.sutopia.starsector.mod.phaseprototype.hullmod;

import org.sutopia.starsector.mod.phaseprototype.PhasePrototypes;

import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class PrototypeAdaptivePhaseCoils extends PhasePrototypeBase {
	
	public static final Float ADAPTIVE_MAX_PENALTY_FLUX_LEVEL = .75f;
	
	@Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		ship.setCustomData(PhasePrototypes.MAX_PENALTY_FLUX_LEVEL_KEY, ADAPTIVE_MAX_PENALTY_FLUX_LEVEL);
	}
	
	@Override
	public String getDescriptionParam(int index, HullSize hullSize) {
		return ADAPTIVE_MAX_PENALTY_FLUX_LEVEL * 100 + "%";
	}
}
