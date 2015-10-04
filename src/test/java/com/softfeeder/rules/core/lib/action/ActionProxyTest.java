/************************************************************************ 
 * Copyright MasterChef
 * ============================================= 
 *          Auther: Ehab Al-Hakawati 
 *              on: Oct 4, 2015 12:53:27 PM
 */
package com.softfeeder.rules.core.lib.action;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.softfeeder.rules.annotation.action.Action;
import com.softfeeder.rules.annotation.action.Do;
import com.softfeeder.rules.core.Priority;
import com.softfeeder.rules.core.RuleContext;
import com.softfeeder.rules.core.exception.InvalidActionDefinition;

public class ActionProxyTest {

	@Test
	public void test() throws InvalidActionDefinition {
		com.softfeeder.rules.core.lib.action.Action action = ActionProxy.asAction(new RuleContext(), new ActionTest());

		assertTrue(action.execute());
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
