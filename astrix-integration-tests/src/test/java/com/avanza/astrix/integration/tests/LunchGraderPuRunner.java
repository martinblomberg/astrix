/*
 * Copyright 2014 Avanza Bank AB
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
package com.avanza.astrix.integration.tests;

import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.avanza.astrix.beans.core.AstrixSettings;
import com.avanza.astrix.gs.test.util.PartitionedPu;
import com.avanza.astrix.gs.test.util.PuConfigurers;
import com.avanza.astrix.provider.component.AstrixServiceComponentNames;

public class LunchGraderPuRunner {
	
	public static void main(String[] args) throws IOException {
		Logger.getRootLogger().setLevel(Level.INFO);
		System.setProperty("com.gs.jini_lus.groups", "lunch-grader-pu");
		AstrixSettings settings = new AstrixSettings();
		settings.setServiceRegistryUri(AstrixServiceComponentNames.GS_REMOTING + ":jini://*/*/service-registry-space?groups=service-registry");
		PartitionedPu partitionedPu = new PartitionedPu(PuConfigurers.partitionedPu("classpath:/META-INF/spring/lunch-grader-pu.xml")
				.numberOfPrimaries(1)
				.contextProperty("configSourceId", settings.getConfigSourceId())
				.numberOfBackups(0));
		partitionedPu.run();
	}
	
}
