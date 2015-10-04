/************************************************************************ 
 * Copyright MasterChef
 * ============================================= 
 *          Auther: Ehab Al-Hakawati 
 *              on: Oct 4, 2015 1:03:51 PM
 */
package com.softfeeder.rules.core;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.softfeeder.rules.annotation.Rule;
import com.softfeeder.rules.annotation.action.Action;
import com.softfeeder.rules.annotation.action.Do;
import com.softfeeder.rules.annotation.condition.Condition;
import com.softfeeder.rules.annotation.condition.When;
import com.softfeeder.rules.core.exception.EngineConstructionFailure;

public class RulesExecutorTest {

	@Test
	public void test() throws EngineConstructionFailure {
		RuleContext context = new RuleContext();
		RulesExecutor executor = new RulesExecutor(context, new RuleAndTest(), new RuleAnyTest());
		assertTrue(!executor.executeWhenAllEvaluated());
	}

	@Rule(name = "Rule Any", conditions = ConditionAny.class, actions = { ActionTest.class })
	public static class RuleAnyTest {

	}

	@Rule(name = "Rule And", conditions = ConditionAnd.class, actions = { ActionTest.class })
	public static class RuleAndTest {

	}

	@Condition(name = "ConditionAny", conjunction = Conjunction.ANY)
	public static class ConditionAny {
		@When
		public boolean test1() {
			return true;
		}

		@When
		public boolean test2() {
			return false;
		}

	}

	@Condition(name = "ConditionAnd", conjunction = Conjunction.AND)
	public static class ConditionAnd {
		@When
		public boolean test1() {
			return true;
		}

		@When
		public boolean test2() {
			return false;
		}

	}

	@Action(name = "Action")
	public static class ActionTest {

		@Do
		public boolean action1() {
			System.out.println("Action1");
			return true;
		}

	}

}
