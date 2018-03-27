package io.openems.edge.ess.symmetric.readonly.api;

import org.osgi.annotation.versioning.ProviderType;

import io.openems.edge.common.channel.Channel;
import io.openems.edge.common.channel.Unit;
import io.openems.edge.ess.api.Ess;

@ProviderType
public interface EssSymmetricReadonly extends Ess {

	public enum ChannelId implements io.openems.edge.common.channel.ChannelDoc {
		ACTIVE_POWER(Unit.W), //
		REACTIVE_POWER(Unit.VAR);

		private final Unit unit;

		private ChannelId(Unit unit) {
			this.unit = unit;
		}

		@Override
		public Unit getUnit() {
			return this.unit;
		}
	}

	default Channel getActivePower() {
		return this.channel(ChannelId.ACTIVE_POWER);
	}

	default Channel getReactivePower() {
		return this.channel(ChannelId.REACTIVE_POWER);
	}
}