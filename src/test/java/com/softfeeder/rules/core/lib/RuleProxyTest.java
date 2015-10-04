/**
 * The MIT License
 *
 *  Copyright (c) 2015, Ehab Al-Hakawati (e.hakawati@softfeeder.com)
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
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

/**
 * 
 * @author Ehab Al-Hakawati
 * @date 04-Oct-2015
 *
 */
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
