/*******************************************************************************
 * OpenEMS - Open Source Energy Management System
 * Copyright (c) 2016, 2017 FENECON GmbH and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 *   FENECON GmbH - initial API and implementation and initial documentation
 *******************************************************************************/
package io.openems.impl.device.socomec;

import io.openems.api.doc.ThingInfo;

@ThingInfo(title = "Socomec")
public class Socomec {
	// MOVED TO OSGi
	//extends ModbusDevice {
	//
	//	/*
	//	 * Constructors
	//	 */
	//	public Socomec(Bridge parent) throws OpenemsException {
	//		super(parent);
	//	}
	//
	//	/*
	//	 * Config
	//	 */
	//	@ChannelInfo(title = "Meter", description = "Sets the meter nature.", type = SocomecMeter.class)
	//	public final ConfigChannel<SocomecMeter> meter = new ConfigChannel<SocomecMeter>("meter", this).addChangeListener(this);
	//
	//	/*
	//	 * Methods
	//	 */
	//	@Override
	//	protected Set<DeviceNature> getDeviceNatures() {
	//		Set<DeviceNature> natures = new HashSet<>();
	//		if (meter.valueOptional().isPresent()) {
	//			natures.add(meter.valueOptional().get());
	//		}
	//		return natures;
	//	}

}
