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
package se.avanzabank.asterix.monitoring;

import java.util.Objects;

import org.kohsuke.MetaInfServices;

import se.avanzabank.asterix.context.AsterixEventLoggerPlugin;
import se.avanzabank.system.graphite.GraphiteFactory;
import se.avanzabank.system.stats.Stats;

/**
 * @author Kristoffer Erlandsson (krierl)
 */
@MetaInfServices(value = AsterixEventLoggerPlugin.class)
public class StatsEventLogger implements AsterixEventLoggerPlugin {

	private Stats stats;

	public StatsEventLogger() {
		this(new GraphiteFactory().getStats());
	}
	
	public StatsEventLogger(Stats stats) {
		this.stats = Objects.requireNonNull(stats);
	}
	
	@Override
	public void increment(String event) {
		stats.increment(event);
	}

}
