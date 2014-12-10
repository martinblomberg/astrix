/*
 * Copyright 2014-2015 Avanza Bank AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.avanza.astrix.context;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 
 * @author Elias Lindholm (elilin)
 *
 */
public class AstrixServiceLookupFactory implements AstrixPluginsAware {
	
	private List<AstrixServiceLookupPlugin<? extends Annotation>> serviceLookupPlugins = new CopyOnWriteArrayList<>();
	private AstrixServiceLookup defaultLookup;

	public AstrixServiceLookup createServiceLookup(AstrixApiDescriptor descriptor) {
		for (AstrixServiceLookupPlugin<?> lookupPlugin : serviceLookupPlugins) {
			if (descriptor.isAnnotationPresent(lookupPlugin.getLookupAnnotationType())) {
				return create(descriptor, lookupPlugin);
			}
		}
		if (defaultLookup != null) {
			return defaultLookup;
		}
		throw new IllegalArgumentException("Can't identify what lookup-strategy to use to locate services exported using descriptor: " + descriptor);
	}

	private <T extends Annotation> AstrixServiceLookup create(AstrixApiDescriptor descriptor, AstrixServiceLookupPlugin<T> lookupPlugin) {
		return AstrixServiceLookup.create(lookupPlugin, descriptor.getAnnotation(lookupPlugin.getLookupAnnotationType()));
	}
	
	@Override
	public void setPlugins(AstrixPlugins plugins) {
		for (AstrixServiceLookupPlugin<?> lookupPlugin : plugins.getPlugins(AstrixServiceLookupPlugin.class)) {
			if (lookupPlugin.getClass().isAnnotationPresent(DefaultServiceLookup.class)) {
				this.defaultLookup = AstrixServiceLookup.create(lookupPlugin, null);
			}
			this.serviceLookupPlugins.add(lookupPlugin);
		}
	}

}