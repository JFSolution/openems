package io.openems.edge.ess.streetscooter;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition( //
		name = "ESS Streetscooter Cluster", //
		description = "Implements the clustering for streetscooter energy storage system.")
@interface ConfigCluster {
	String service_pid();

	String id() default "cluster0"; //cluster0? ess0?

	boolean enabled() default true;

	 @AttributeDefinition(name = "ESS-IDs", description = "IDs of single ESS systems.", cardinality = Integer.MAX_VALUE, type = AttributeType.STRING, defaultValue= {"ess0", "ess1"})
	 String[] ess_ids() default {};

	 @AttributeDefinition(name = "ESS target filter", description = "This is auto-generated by 'ESS-IDs'." )
	 String Ess_target() default "";

	String webconsole_configurationFactory_nameHint() default "ESS Streetscooter Cluster [{id}]";
}