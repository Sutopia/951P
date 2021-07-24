package org.sutopia.starsector.mod.phaseprototype.hullmod;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class PrototypePhaseAnchor extends PhasePrototypeBase {
	
	public static final float TIME_DILATION_VALUE = 4f;
	private static final float PHASE_TIME_BONUS_MULT_PERCENT_MOD = 50f;
	
	@Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getDynamic().getMod(Stats.PHASE_TIME_BONUS_MULT).modifyPercent(id, PHASE_TIME_BONUS_MULT_PERCENT_MOD);
	}
	
	@Override
	public String getDescriptionParam(int index, HullSize hullSize) {
		return TIME_DILATION_VALUE + "";
	}
}
