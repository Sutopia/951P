package org.sutopia.starsector.mod.phaseprototype.hullmod;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;

public abstract class PhasePrototypeBase extends BaseHullMod {

	@Override
	public boolean isApplicableToShip(ShipAPI ship) {
		if (ship == null) return false;
		if (!ship.getVariant().hasHullMod(HullMods.PHASE_FIELD)) return false;
		
		return true; 
	}

	@Override
	public String getUnapplicableReason(ShipAPI ship) {
		if (!ship.getVariant().hasHullMod(HullMods.PHASE_FIELD)) return "This hullmod is only applicable on phase ship";
		
		return "Something went wrong"; 
	}

}
