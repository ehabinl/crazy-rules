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
package com.softfeeder.rules.core.lib.action;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.softfeeder.rules.annotation.action.Action;
import com.softfeeder.rules.annotation.action.Do;
import com.softfeeder.rules.core.Priority;
import com.softfeeder.rules.core.RuleContext;
import com.softfeeder.rules.core.exception.InvalidActionDefinition;

/**
 * 
 * @author Ehab Al-Hakawati
 * @date 04-Oct-2015
 *
 */
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
