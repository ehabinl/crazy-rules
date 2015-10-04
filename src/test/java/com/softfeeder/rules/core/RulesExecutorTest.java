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
package com.softfeeder.rules.core;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.softfeeder.rules.annotation.Rule;
import com.softfeeder.rules.annotation.action.Action;
import com.softfeeder.rules.annotation.action.Do;
import com.softfeeder.rules.annotation.condition.Condition;
import com.softfeeder.rules.annotation.condition.When;
import com.softfeeder.rules.core.exception.EngineConstructionFailure;

/**
 * 
 * @author Ehab Al-Hakawati
 * @date 04-Oct-2015
 *
 */
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
