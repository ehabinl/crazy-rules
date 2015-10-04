/************************************************************************ 
 * Copyright MasterChef
 * ============================================= 
 *          Auther: Ehab Al-Hakawati 
 *              on: Oct 4, 2015 12:47:24 PM
 */
package com.softfeeder.rules.core.lib;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.softfeeder.rules.annotation.action.Action;
import com.softfeeder.rules.annotation.action.Do;
import com.softfeeder.rules.annotation.condition.Condition;
import com.softfeeder.rules.annotation.condition.When;
import com.softfeeder.rules.core.Conjunction;
import com.softfeeder.rules.core.Priority;
import com.softfeeder.rules.core.RuleContext;
import com.softfeeder.rules.core.exception.InvalidActionDefinition;
import com.softfeeder.rules.core.exception.InvalidConditionDefinition;
import com.softfeeder.rules.core.exception.InvalidRuleDefinition;

public class RuleProxyTest {

	@Test
	public void test() throws InvalidConditionDefinition, InvalidActionDefinition, InvalidRuleDefinition {
		Rule rule = (Rule) RuleProxy.asRule(new RuleContext(), new RuleTest());
		assertTrue(rule.evaluate());
		assertTrue(rule.execute());
	}

	@com.softfeeder.rules.annotation.Rule(name = "EHABs", conditions = ConditionTest.class, actions = { ActionTest.class })
	public static class RuleTest {

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

	@Action(name = "Action1")
	public static class ActionTest {

		@Do
		public boolean action1() {
			System.out.println("Action1");
			return true;
		}

		@Do
		public boolean action2() {
			System.out.println("Action2");
			return true;
		}

		@Do(priority = Priority.HIGHEST)
		public void action3() {
			System.out.println("Action3");
		}
	}

}
