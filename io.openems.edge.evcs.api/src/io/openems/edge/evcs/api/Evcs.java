package io.openems.edge.evcs.api;

import org.osgi.annotation.versioning.ProviderType;

import io.openems.common.types.OpenemsType;
import io.openems.edge.common.channel.WriteChannel;
import io.openems.edge.common.channel.doc.Doc;
import io.openems.edge.common.channel.doc.Unit;
import io.openems.edge.common.component.OpenemsComponent;

@ProviderType
public interface Evcs extends OpenemsComponent {

	public enum ChannelId implements io.openems.edge.common.channel.doc.ChannelId {
		/**
		 * Set Charge Power
		 * 
		 * <ul>
		 * <li>Interface: Evcs
		 * <li>Writable
		 * <li>Type: Integer
		 * <li>Unit: W
		 * </ul>
		 */
		SET_CHARGE_POWER(new Doc().setWritable().type(OpenemsType.INTEGER).unit(Unit.WATT)),
		/**
		 * Set Display Text
		 * 
		 * <ul>
		 * <li>Interface: Evcs
		 * <li>Writable
		 * <li>Type: String
		 * </ul>
		 */
		SET_DISPLAY_TEXT(new Doc().setWritable().type(OpenemsType.STRING));

		// TODO add debug channel for set charge power

		private final Doc doc;

		private ChannelId(Doc doc) {
			this.doc = doc;
		}

		@Override
		public Doc doc() {
			return this.doc;
		}
	}

	/**
	 * Sets the allowed maximum charge power of the EVCS in [W].
	 * 
	 * Actual charge power depends on
	 * <ul>
	 * <li>whether the electric vehicle is connected at all and ready for charging
	 * <li>limitation of electric vehicle
	 * <li>limitation of power line
	 * <li>...
	 * </ul>
	 * 
	 * @return
	 */
	public default WriteChannel<Integer> setChargePower() {
		return this.channel(ChannelId.SET_CHARGE_POWER);
	}

	/**
	 * Sets a Text that is shown at the display of the EVCS. Be aware that the EVCS
	 * might not have a display or the text might be restricted.
	 * 
	 * @return
	 */
	public default WriteChannel<String> setDisplayText() {
		return this.channel(ChannelId.SET_DISPLAY_TEXT);
	}
}
