package org.sutopia.starsector.mod.phaseprototype.system;

import org.sutopia.starsector.mod.phaseprototype.PhasePrototypes;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.FluxTrackerAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class PhaseCloakStats95_1 extends PhaseCloakStats95a {
	public static final float DEFAULT_MAX_PENALTY_FLUX_LEVEL = 0.5f;
	public static final float MAX_SPEED_PENALTY = 2f/3f;

	@Override
	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		ShipAPI ship = null;
		boolean player = false;
		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();
			player = ship == Global.getCombatEngine().getPlayerShip();
			id = id + "_" + ship.getId();
		} else {
			return;
		}
		
		if (player) {
			maintainStatus(ship, state, effectLevel);
		}
		
		if (Global.getCombatEngine().isPaused()) {
			return;
		}
		
		if (state == State.COOLDOWN || state == State.IDLE) {
			unapply(stats, id);
			return;
		}
		
		// changed to be more precise
		float speedPercentMod = stats.getDynamic().getMod(Stats.PHASE_CLOAK_SPEED_MOD).getFlatBonus(); // IDK WHY THIS IS A THING
		stats.getMaxSpeed().modifyPercent(id, speedPercentMod * effectLevel);
		
		// .95.1 feature
		float maxPenaltyHardFluxLevel = DEFAULT_MAX_PENALTY_FLUX_LEVEL;
		if (ship.getCustomData().get(PhasePrototypes.MAX_PENALTY_FLUX_LEVEL_KEY) instanceof Float) {
			maxPenaltyHardFluxLevel = (float)ship.getCustomData().get(PhasePrototypes.MAX_PENALTY_FLUX_LEVEL_KEY);
		}
		FluxTrackerAPI flux = ship.getFluxTracker();
		float fluxLevel = flux.getHardFlux() / flux.getMaxFlux();
		float penaltyLevel = effectLevel;
		if (fluxLevel <= 0) {
			penaltyLevel = 0f;
		} else if (fluxLevel < maxPenaltyHardFluxLevel) {
			penaltyLevel *= fluxLevel / maxPenaltyHardFluxLevel;
		}
		stats.getMaxSpeed().modifyMult(id, 1f - (penaltyLevel * MAX_SPEED_PENALTY));
		
		
		float level = effectLevel;
		
		float levelForAlpha = level;
		
		ShipSystemAPI cloak = ship.getPhaseCloak();
		if (cloak == null) cloak = ship.getSystem();
		
		
		if (state == State.IN || state == State.ACTIVE) {
			ship.setPhased(true);
			levelForAlpha = level;
		} else if (state == State.OUT) {
			if (level > 0.5f) {
				ship.setPhased(true);
			} else {
				ship.setPhased(false);
			}
			levelForAlpha = level;
		}

		
		ship.setExtraAlphaMult(1f - (1f - SHIP_ALPHA_MULT) * levelForAlpha);
		ship.setApplyExtraAlphaToEngines(true);

		float shipTimeMult = 1f + (getMaxTimeMult(stats) - 1f) * levelForAlpha;
		stats.getTimeMult().modifyMult(id, shipTimeMult);
		if (player) {
			Global.getCombatEngine().getTimeMult().modifyMult(id, 1f / shipTimeMult);

		} else {
			Global.getCombatEngine().getTimeMult().unmodify(id);
		}
		
	}

	@Override
	public void unapply(MutableShipStatsAPI stats, String id) {
		ShipAPI ship = null;
		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();
		} else {
			return;
		}
		
		Global.getCombatEngine().getTimeMult().unmodify(id);
		stats.getTimeMult().unmodify(id);
		
		stats.getMaxSpeed().unmodify(id);
		
		ship.setPhased(false);
		ship.setExtraAlphaMult(1f);
	}

}
