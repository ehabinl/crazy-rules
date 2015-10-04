/************************************************************************ 
 * Copyright MasterChef
 * ============================================= 
 *          Auther: Ehab Al-Hakawati 
 *              on: Oct 4, 2015 12:58:49 PM
 */
package com.softfeeder.rules.core;

import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.junit.Test;

import com.softfeeder.rules.annotation.condition.Condition;

public class UtilsTest {

	@Test
	public void test() {
		
		ConditionTest conditionTest = new ConditionTest();
		RuleContext context = new RuleContext();
		context.addValue("string", "test_value");

		Utils.fieldsInjection(context, conditionTest);

		assertTrue("test_value".equals(conditionTest.getString()));
	}

	@Condition(name = "Condition1")
	public static class ConditionTest {

		@Inject
		private String string;

		public String getString() {
			return this.string;
		}

		public void setString(String string) {
			this.string = string;
		}

	}

}
