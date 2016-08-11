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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.softfeeder.rules.annotation.Rule;
import com.softfeeder.rules.core.Conjunction;
import com.softfeeder.rules.core.RuleContext;
import com.softfeeder.rules.core.Utils;
import com.softfeeder.rules.core.exception.InvalidActionDefinition;
import com.softfeeder.rules.core.exception.InvalidConditionDefinition;
import com.softfeeder.rules.core.exception.InvalidRuleDefinition;
import com.softfeeder.rules.core.lib.action.Action;
import com.softfeeder.rules.core.lib.action.ActionProxy;
import com.softfeeder.rules.core.lib.condition.Condition;
import com.softfeeder.rules.core.lib.condition.ConditionProxy;

/**
 * 
 * @author Ehab Al-Hakawati
 * @since 04-Oct-2015
 *
 */
public class RuleProxy implements InvocationHandler {

	private static final Logger LOG = LoggerFactory.getLogger(RuleProxy.class.getName());

	private final RuleContext context;
	private final Object target;

	private final List<Condition> listOfConditions;
	private final List<Action> listOfActions;

	/**
	 * @param context
	 * @param target
	 * @throws InvalidConditionDefinition
	 */
	public RuleProxy(RuleContext context, Object target) throws InvalidConditionDefinition, InvalidActionDefinition,
			InvalidRuleDefinition {

		LOG.info(String.format("Create rule proxy {%s}", target.getClass().getName()));

		if (!target.getClass().isAnnotationPresent(Rule.class)) {
			throw new InvalidRuleDefinition(String.format("Invalid rule class {%s}", target.getClass().getName()));
		}

		this.context = context;
		this.target = target;
		this.listOfConditions = new ArrayList<>();
		this.listOfActions = new ArrayList<>();

		// -- add all condition
		try {
			for (Class<?> clzz : getAnnotation().conditions()) {
				LOG.info(String.format("Add condition {%s}", clzz.getName()));
				if (clzz.equals(Condition.class)) {
					Condition condition = (Condition) clzz.newInstance();
					Utils.fieldsInjection(this.context, condition);
					this.listOfConditions.add(condition);
				} else if (clzz.isAnnotationPresent(com.softfeeder.rules.annotation.condition.Condition.class)) {
					this.listOfConditions.add(ConditionProxy.asCondition(this.context, clzz.newInstance()));
				} else {
					throw new InvalidConditionDefinition(String.format("Invalid condition class {%s}", clzz.getName()));
				}
			}
		} catch (InstantiationException | IllegalAccessException ex) {
			LOG.error("Construction failure");
		}

		// -- add all actions
		try {
			for (Class<?> clzz : getAnnotation().actions()) {
				LOG.info(String.format("Add action {%s}", clzz.getName()));
				if (clzz.equals(Action.class)) {
					Action action = (Action) clzz.newInstance();
					Utils.fieldsInjection(this.context, action);
					this.listOfActions.add(action);
				} else if (clzz.isAnnotationPresent(com.softfeeder.rules.annotation.action.Action.class)) {
					this.listOfActions.add(ActionProxy.asAction(context, clzz.newInstance()));
				} else {
					throw new InvalidActionDefinition(String.format("Invalid action class {%s}", clzz.getName()));
				}
			}
		} catch (InstantiationException | IllegalAccessException ex) {
			LOG.error("Construction failure");
		}
	}

	/**
	 * 
	 * @param context
	 * @param rule
	 * @return new rule proxy
	 * @throws InvalidConditionDefinition
	 * @throws InvalidActionDefinition
	 * @throws InvalidRuleDefinition
	 */
	public static com.softfeeder.rules.core.lib.Rule asRule(final RuleContext context, final Object rule)
			throws InvalidConditionDefinition, InvalidActionDefinition, InvalidRuleDefinition {

		return (com.softfeeder.rules.core.lib.Rule) Proxy.newProxyInstance(
				com.softfeeder.rules.core.lib.Rule.class.getClassLoader(),
				new Class[] { com.softfeeder.rules.core.lib.Rule.class }, new RuleProxy(context, rule));
	}

	/**
	 * @param proxy
	 * @param method
	 * @param args
	 * @return invoker return value
	 * @throws Throwable
	 */
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		switch (method.getName()) {
		case "getName":
			return getAnnotation().name();
		case "getDescription":
			return getAnnotation().description();
		case "getConditions":
			return this.listOfConditions;
		case "addCondition":
			return this.listOfConditions.add((Condition) args[0]);
		case "addAction":
			return this.listOfActions.add((Action) args[0]);
		case "getActions":
			return this.listOfActions;
		case "evaluate":
			return evaluate(getAnnotation().conjunction());
		case "execute":
			return execute();
		case "equals":
			return target.equals(args[0]);
		case "hashCode":
			return target.hashCode();
		case "toString":
			return target.toString();
		}
		return null;

	}

	/**
	 * @param conjunction
	 * @return evaluation result
	 */
	private boolean evaluate(Conjunction conjunction) {
		boolean result = true;

		for (Condition condition : this.listOfConditions) {
			boolean invoker = condition.evaluate();
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
	 * 
	 * @return execution result
	 */
	private boolean execute() {
		boolean result = true;
		for (Action action : this.listOfActions) {
			result &= action.execute();
		}
		return result;
	}

	/**
	 * @return class annotation
	 */
	private com.softfeeder.rules.annotation.Rule getAnnotation() {
		return target.getClass().getAnnotation(com.softfeeder.rules.annotation.Rule.class);
	}
}
