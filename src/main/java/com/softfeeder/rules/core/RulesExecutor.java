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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.softfeeder.rules.core.exception.EngineConstructionFailure;
import com.softfeeder.rules.core.exception.InvalidActionDefinition;
import com.softfeeder.rules.core.exception.InvalidConditionDefinition;
import com.softfeeder.rules.core.exception.InvalidRuleDefinition;
import com.softfeeder.rules.core.lib.Rule;
import com.softfeeder.rules.core.lib.RuleProxy;

/**
 * 
 * @author Ehab Al-Hakawati
 * @date 04-Oct-2015
 *
 */
public class RulesExecutor {

	private static final Logger LOG = Logger.getLogger(RulesExecutor.class.getName());
	private final List<Rule> rules = new ArrayList<>();
	private final RuleContext context;

	/**
	 * 
	 * @param context
	 * @param rules
	 * @throws EngineConstructionFailure
	 */
	public RulesExecutor(final RuleContext context, final Object... rules) throws EngineConstructionFailure {
		this.context = context;

		try {
			for (Object rule : rules) {
				addRule(rule);
			}
		} catch (InvalidActionDefinition | InvalidConditionDefinition | InvalidRuleDefinition ex) {
			throw new EngineConstructionFailure("Invalid rule(s)", ex);
		}
	}

	/**
	 * Add new rule
	 * 
	 * @param rule
	 * @throws InvalidConditionDefinition
	 * @throws InvalidActionDefinition
	 * @throws InvalidRuleDefinition
	 */
	public void addRule(Object rule) throws InvalidConditionDefinition, InvalidActionDefinition, InvalidRuleDefinition {
		if (rule instanceof Rule) {
			this.rules.add((Rule) rule);
		} else {
			this.rules.add(RuleProxy.asRule(this.context, rule));
		}
	}

	/**
	 * 
	 * @return
	 */
	public boolean executeWhenAllEvaluated() {

		boolean result = true;
		for (Rule rule : this.rules) {
			result &= rule.evaluate();
			LOG.info(String.format("Evaluate %s {%b}", rule.getName(), result));
			if (!result) {
				return false;
			}
		}

		for (Rule rule : this.rules) {
			LOG.info(String.format("Execute %s {%b}", rule.getName(), result));
		}

		return true;
	}

	/**
	 * Evaluate all rules
	 */
	public void executeAll() {
		for (Rule rule : this.rules) {
			final boolean result = rule.evaluate();
			LOG.info(String.format("Evaluate %s {%b}", rule.getName(), result));
			if (result) {
				LOG.info(String.format("Execute %s {%b}", rule.getName(), rule.execute()));
			}
		}
	}
}
