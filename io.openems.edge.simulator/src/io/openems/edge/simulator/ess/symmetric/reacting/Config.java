package io.openems.edge.simulator.ess.symmetric.reacting;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition( //
		name = "Simulator EssSymmetric Reacting", //
		description = "This simulates a 'reacting' symmetric Energy Storage System.")
@interface Config {
	String service_pid();

	String id() default "ess0";

	boolean enabled() default true;

	@AttributeDefinition(name = "Max Apparant Power [VA]")
	int maxApparentPower() default 10000;

	@AttributeDefinition(name = "Capacity [Wh]")
	int capacity() default 10000;

	@AttributeDefinition(name = "Initial State of Charge [%]")
	int initialSoc() default 50;

	String webconsole_configurationFactory_nameHint() default "Simulator EssSymmetric Reacting [{id}]";
}