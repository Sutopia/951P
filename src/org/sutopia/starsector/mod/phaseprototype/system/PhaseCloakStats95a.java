package org.sutopia.starsector.mod.phaseprototype.system;


import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;

// The code is directly copied from Alex's code of 0.95a Starsector
public class PhaseCloakStats95a extends BaseShipSystemScript {
		
	public static final float SHIP_ALPHA_MULT = 0.25f;
	public static final float VULNERABLE_FRACTION = 0f;
	public static final float INCOMING_DAMAGE_MULT = 0.25f;
	
	public static final float MAX_TIME_MULT = 3f;
	
	
	protected Object STATUSKEY1 = new Object();
	protected Object STATUSKEY2 = new Object();
	protected Object STATUSKEY3 = new Object();
	protected Object STATUSKEY4 = new Object();
	
	
	public static float getMaxTimeMult(MutableShipStatsAPI stats) {
		return 1f + (MAX_TIME_MULT - 1f) * stats.getDynamic().getValue(Stats.PHASE_TIME_BONUS_MULT);
	}
	
	protected void maintainStatus(ShipAPI playerShip, State state, float effectLevel) {
		float level = effectLevel;
		float f = VULNERABLE_FRACTION;
		
		ShipSystemAPI cloak = playerShip.getPhaseCloak();
		if (cloak == null) cloak = playerShip.getSystem();
		if (cloak == null) return;
		
		if (level > f) {
			Global.getCombatEngine().maintainStatusForPlayerShip(STATUSKEY2,
					cloak.getSpecAPI().getIconSpriteName(), cloak.getDisplayName(), "time flow altered", false);
		} else {
		}
	}
	
	
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


	public void unapply(MutableShipStatsAPI stats, String id) {
		ShipAPI ship = null;
		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();
		} else {
			return;
		}
		
		Global.getCombatEngine().getTimeMult().unmodify(id);
		stats.getTimeMult().unmodify(id);
		
		stats.getMaxSpeed().unmodifyPercent(id);
		
		ship.setPhased(false);
		ship.setExtraAlphaMult(1f);
	}
	
	public StatusData getStatusData(int index, State state, float effectLevel) {
		return null;
	}
}