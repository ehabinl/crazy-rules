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
package com.softfeeder.rules.core.lib.condition;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import com.softfeeder.rules.annotation.condition.Condition;
import com.softfeeder.rules.core.Conjunction;
import com.softfeeder.rules.core.RuleContext;
import com.softfeeder.rules.core.Utils;
import com.softfeeder.rules.core.exception.InvalidConditionDefinition;

/**
 * 
 * @author Ehab Al-Hakawati
 * @date 04-Oct-2015
 *
 */
public class ConditionProxy implements InvocationHandler {

	private static final Logger LOG = Logger.getLogger(ConditionProxy.class.getName());

	private final Set<Method> evaluateMethods;
	private final RuleContext context;
	private final Object target;

	/**
	 * @param context
	 * @param target
	 * @throws InvalidConditionDefinition
	 */
	public ConditionProxy(RuleContext context, Object target) throws InvalidConditionDefinition {

		LOG.info(String.format("Create condition proxy {%s}", target.getClass().getName()));

		if (!target.getClass().isAnnotationPresent(Condition.class)) {
			throw new InvalidConditionDefinition(String.format("Invalid condition class {%s}", target.getClass().getName()));
		}

		this.context = context;
		this.target = target;
		this.evaluateMethods = new HashSet<>();

		Utils.fieldsInjection(this.context, this.target);

		// -- cache target methods

		for (Method method : getMethods()) {
			if (method.isAnnotationPresent(com.softfeeder.rules.annotation.condition.When.class)) {
				evaluateMethods.add(method);
			}
		}
	}

	/**
	 * @param context
	 * @param condition
	 * @return
	 * @throws InvalidConditionDefinition
	 * @throws
	 */
	public static com.softfeeder.rules.core.lib.condition.Condition asCondition(final RuleContext context,
			final Object condition) throws InvalidConditionDefinition {

		return (com.softfeeder.rules.core.lib.condition.Condition) Proxy.newProxyInstance(
				com.softfeeder.rules.core.lib.condition.Condition.class.getClassLoader(), new Class[] {
						com.softfeeder.rules.core.lib.condition.Condition.class, Comparable.class }, new ConditionProxy(
						context, condition));
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
		case "getConjunction":
			return getAnnotation().conjunction();
		case "evaluate":
			return evaluate(args, getAnnotation().conjunction());
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
				com.softfeeder.rules.core.lib.condition.Condition otherRule = (com.softfeeder.rules.core.lib.condition.Condition) args[0];
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
	 * @param conjunction
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private boolean evaluate(Object[] args, Conjunction conjunction) throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		boolean result = true;
		for (Method method : evaluateMethods) {
			boolean invoker = Boolean.valueOf(method.invoke(target, args).toString());
			if (invoker && conjunction == Conjunction.ANY) {
				return true;
			}
			result &= invoker;
		}
		if (conjunction == Conjunction.NONE) {
			return !result;
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
	private Condition getAnnotation() {
		return target.getClass().getAnnotation(Condition.class);
	}

	/**
	 * @param otherRule
	 * @return
	 * @throws Exception
	 */
	private int compareTo(final com.softfeeder.rules.core.lib.condition.Condition otherRule) {
		String otherName = otherRule.getName();
		String name = getAnnotation().name();
		return name.compareTo(otherName);
	}
}
