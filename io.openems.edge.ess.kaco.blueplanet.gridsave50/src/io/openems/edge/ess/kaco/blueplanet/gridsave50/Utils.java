package io.openems.edge.ess.kaco.blueplanet.gridsave50;

import java.util.Arrays;
import java.util.stream.Stream;

import io.openems.edge.common.channel.AbstractReadChannel;
import io.openems.edge.common.channel.IntegerReadChannel;
import io.openems.edge.common.channel.IntegerWriteChannel;
import io.openems.edge.common.channel.StateCollectorChannel;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.ess.api.ManagedSymmetricEss;
import io.openems.edge.ess.api.SymmetricEss;

public class Utils {
	public static Stream<? extends AbstractReadChannel<?>> initializeChannels(EssKacoBlueplanetGridsave50 ess) {
		// Define the channels. Using streams + switch enables Eclipse IDE to tell us if
		// we are missing an Enum value.
		return Stream.of( //
				Arrays.stream(OpenemsComponent.ChannelId.values()).map(channelId -> {
					switch (channelId) {
					case STATE:
						return new StateCollectorChannel(ess, channelId);
					}
					return null;
				}), Arrays.stream(SymmetricEss.ChannelId.values()).map(channelId -> {
					switch (channelId) {
					case SOC:
					case ACTIVE_POWER:
					case REACTIVE_POWER:
						return new IntegerReadChannel(ess, channelId);
					case MAX_ACTIVE_POWER:
						return new IntegerReadChannel(ess, channelId, EssKacoBlueplanetGridsave50.MAX_APPARENT_POWER);
					case GRID_MODE:
						return new IntegerReadChannel(ess, channelId, SymmetricEss.GridMode.UNDEFINED.ordinal());
					}
					return null;
				}), Arrays.stream(ManagedSymmetricEss.ChannelId.values()).map(channelId -> {
					switch (channelId) {
					case DEBUG_SET_ACTIVE_POWER:
					case DEBUG_SET_REACTIVE_POWER:
						return new IntegerReadChannel(ess, channelId);
					}
					return null;
				}), Arrays.stream(EssKacoBlueplanetGridsave50.ChannelId.values()).map(channelId -> {
					switch (channelId) {
//					case CHA_CUTOFF_A:
					case CHA_MAX_A:
					case CHA_MAX_V:
//					case DIS_CUTOFF_A:
					case DIS_MAX_A:
					case DIS_MIN_V:
					case EN_LIMIT:
					case W_SET_PCT:
					case REQUESTED_STATE:
					case BAT_SOC_:
					case BAT_SOH:
					case BAT_TEMP:
					case COMMAND_ID_REQ:
					case REQ_PARAM_0:
					case COMMAND_ID_REQ_ENA:
						return new IntegerWriteChannel(ess, channelId);
					case A_SF:
					case V_SF:
					case VENDOR_OPERATING_STATE:
					case CURRENT_STATE:
					case WATCHDOG:
					case W_MAX:
					case W_MAX_SF:
					case W_SET_PCT_SF:
					case SOC_SF:
					case SOH_SF:
					case TEMP_SF:
					case COMMAND_ID_RES:
					case RETURN_CODE:
						return new IntegerReadChannel(ess, channelId);
					}
					return null;
				}) //
		).flatMap(channel -> channel);
	}
}
