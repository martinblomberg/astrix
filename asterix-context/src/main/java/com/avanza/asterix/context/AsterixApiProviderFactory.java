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
package com.avanza.asterix.context;

import java.util.ArrayList;
import java.util.List;


/**
 * This component is used to create runtime factory representations (AsterixApiProvider) for api's hooked
 * into asterix. An API is defined by an AsterixApiDescriptor, which in turn uses different annotations for
 * different types of apis. This class is responsible for interpreting such annotations and create an
 * AsterixApiProvider for the given api. <p>
 * 
 * The factory is extendable by adding more {@link AsterixApiProviderPlugin}'s. <p>
 *  
 * @author Elias Lindholm (elilin)
 *
 */
public class AsterixApiProviderFactory {
	
	private final AsterixApiProviderPlugins apiProviderPlugins;
	
	public AsterixApiProviderFactory(AsterixApiProviderPlugins apiProviderPlugins) {
		this.apiProviderPlugins = apiProviderPlugins;
	}
	
	public AsterixApiProvider create(AsterixApiDescriptor descriptor) {
		AsterixApiProviderPlugin providerFactoryPlugin = getProviderPlugin(descriptor);
		List<AsterixFactoryBean<?>> factoryBeans = new ArrayList<>();
		for (AsterixFactoryBeanPlugin<?> factoryBean : providerFactoryPlugin.createFactoryBeans(descriptor)) {
			AsterixFactoryBeanPlugin<?> decoratatedFactory = factoryBean;
			if (!providerFactoryPlugin.isLibraryProvider()) {
				decoratatedFactory = new StatefulAsterixFactoryBean<>(factoryBean);
			}
			factoryBeans.add(new AsterixFactoryBean<>(decoratatedFactory, descriptor, providerFactoryPlugin.isLibraryProvider()));
		}
		return new AsterixApiProvider(factoryBeans, descriptor, providerFactoryPlugin); 
	}
	
	private AsterixApiProviderPlugin getProviderPlugin(AsterixApiDescriptor descriptor) {
		return this.apiProviderPlugins.getProviderPlugin(descriptor);
	}
	
}