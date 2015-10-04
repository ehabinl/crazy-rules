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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import com.softfeeder.rules.annotation.action.Action;
import com.softfeeder.rules.annotation.action.Do;
import com.softfeeder.rules.core.RuleContext;
import com.softfeeder.rules.core.Utils;
import com.softfeeder.rules.core.exception.InvalidActionDefinition;
import com.softfeeder.rules.core.lib.condition.Condition;

/**
 * 
 * @author Ehab Al-Hakawati
 * @date 04-Oct-2015
 *
 */
public class ActionProxy implements InvocationHandler {

	private static final Logger LOG = Logger.getLogger(ActionProxy.class.getName());

	private final List<Method> executeMethods;
	private final RuleContext context;
	private final Object target;

	/**
	 * @param context
	 * @param target
	 * @throws InvalidActionDefinition
	 */
	public ActionProxy(RuleContext context, Object target) throws InvalidActionDefinition {

		LOG.info(String.format("Create action proxy {%s}", target.getClass().getName()));

		if (!target.getClass().isAnnotationPresent(Action.class)) {
			throw new InvalidActionDefinition(String.format("Invalid action class {%s}", target.getClass().getName()));
		}

		this.context = context;
		this.target = target;
		this.executeMethods = new ArrayList<>();

		Utils.fieldsInjection(this.context, this.target);

		// -- cache target methods
		for (Method method : getMethods()) {
			if (method.isAnnotationPresent(com.softfeeder.rules.annotation.action.Do.class)) {
				executeMethods.add(method);
			}
		}

		// -- sort by priority
		Collections.sort(executeMethods, new Comparator<Method>() {
			@Override
			public int compare(Method method1, Method method2) {
				return method1.getAnnotation(Do.class).priority().compareTo(method2.getAnnotation(Do.class).priority());
			}
		});
	}

	/**
	 * @param context
	 * @param action
	 * @return
	 * @throws InvalidActionDefinition
	 */
	public static com.softfeeder.rules.core.lib.action.Action asAction(final RuleContext context, final Object action)
			throws InvalidActionDefinition {

		return (com.softfeeder.rules.core.lib.action.Action) Proxy
				.newProxyInstance(com.softfeeder.rules.core.lib.action.Action.class.getClassLoader(), new Class[] {
						com.softfeeder.rules.core.lib.action.Action.class, Comparable.class }, new ActionProxy(context, action));
	}

	/**
	 * @param proxy
	 * @param method
	 * @param args
	 * @return
	 * @throws Throwable
	 */
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		switch (method.getName()) {
		case "getName":
			return getAnnotation().name();
		case "getDescription":
			return getAnnotation().description();
		case "getPriority":
			return getAnnotation().priority();
		case "execute":
			return execute(args);
		case "equals":
			return target.equals(args[0]);
		case "hashCode":
			return target.hashCode();
		case "toString":
			return target.toString();
		case "compareTo":
			Method compareToMethod = getCompareToMethod();
			if (compareToMethod != null) {
				return compareToMethod.invoke(target, args);
			} else {
				Condition otherRule = (Condition) args[0];
				return compareTo(otherRule);
			}
		}
		return null;

	}

	/**
	 * @return
	 */
	private Method getCompareToMethod() {
		Method[] methods = getMethods();
		for (Method method : methods) {
			if (method.getName().equals("compareTo")) {
				return method;
			}
		}
		return null;
	}

	/**
	 * @param args
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private boolean execute(Object[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		boolean result = true;
		for (Method method : executeMethods) {
			Object output = method.invoke(target, args);
			if (output instanceof Boolean) {
				result &= (Boolean) output;
			}
		}
		return result;
	}

	/**
	 * @return
	 */
	private Method[] getMethods() {
		return target.getClass().getMethods();
	}

	/**
	 * @return
	 */
	private Action getAnnotation() {
		return target.getClass().getAnnotation(Action.class);
	}

	/**
	 * @param otherRule
	 * @return
	 * @throws Exception
	 */
	private int compareTo(final Condition otherRule) {
		String otherName = otherRule.getName();
		String name = getAnnotation().name();
		return name.compareTo(otherName);
	}
}
