/************************************************************************ 
 * Copyright MasterChef
 * ============================================= 
 *          Auther: Ehab Al-Hakawati 
 *              on: Oct 4, 2015 12:55:47 PM
 */
package com.softfeeder.rules.core.lib.condition;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.softfeeder.rules.annotation.condition.Condition;
import com.softfeeder.rules.annotation.condition.When;
import com.softfeeder.rules.core.Conjunction;
import com.softfeeder.rules.core.RuleContext;
import com.softfeeder.rules.core.exception.InvalidConditionDefinition;

public class ConditionProxyTest {

	@Test
	public void test() throws InvalidConditionDefinition {
		com.softfeeder.rules.core.lib.condition.Condition condition = ConditionProxy.asCondition(new RuleContext(),
				new ConditionTest());

		assertTrue(condition.evaluate());
	}

	@Condition(name = "Condition1", conjunction = Conjunction.ANY)
	public static class ConditionTest {
		@When
		public boolean test() {
			return true;
		}

		@When
		public boolean test2() {
			return false;
		}

	}

}
