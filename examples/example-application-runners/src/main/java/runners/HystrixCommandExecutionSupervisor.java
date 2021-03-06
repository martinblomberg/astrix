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
package runners;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import lunch.api.LunchRestaurant;
import lunch.api.LunchService;
import lunch.api.LunchServiceAsync;

import com.avanza.astrix.beans.core.AstrixSettings;
import com.avanza.astrix.context.AstrixConfigurer;
import com.avanza.astrix.context.AstrixContext;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties.ExecutionIsolationStrategy;
import com.netflix.hystrix.HystrixEventType;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier;

public class HystrixCommandExecutionSupervisor {
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		
		HystrixPlugins.getInstance().registerEventNotifier(new HystrixEventNotifier() {
			@Override
			public void markCommandExecution(HystrixCommandKey key,
					ExecutionIsolationStrategy isolationStrategy, int duration,
					List<HystrixEventType> eventsDuringExecution) {
				System.out.println("Executed command: " + key.name() + ", isolationStrategy=" + isolationStrategy + ", duration: " + duration + ", events: " + eventsDuringExecution);
			}
			@Override
			public void markEvent(HystrixEventType eventType, HystrixCommandKey key) {
				System.out.println("HystrixEvent command: " + key.name() + " type: " + eventType);
			}
		});
		
		
		
		AstrixConfigurer astrixConfigurer = new AstrixConfigurer();
		astrixConfigurer.set(AstrixSettings.SERVICE_REGISTRY_URI, "gs-remoting:jini://*/*/service-registry-space?groups=" + Config.LOOKUP_GROUP_NAME);
		AstrixContext context = astrixConfigurer.configure();

		
		LunchService lunchService = context.getBean(LunchService.class);

		while(true) {
			try {
				System.out.println("Checking restaurant count");
				List<LunchRestaurant> allRestaurants = lunchService.getAllLunchRestaurants();
				System.out.println("Restaurant Count: " + allRestaurants.size());
			} catch (Exception e) {
				System.out.println("Failed to get restaurant count");
				e.printStackTrace();
			}
			Thread.sleep(1000);
		}
	}

}
