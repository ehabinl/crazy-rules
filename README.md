Crazy-Rules!
===================
Because rules should be as easy as 1, 2, 3

Condition is 

    @Condition(name = "Condition 1", conjunction = Conjunction.ANY)
	public class Condition {
		@When
		public boolean test1() {
			return true;
		}

		@When
		public boolean test2() {
			return false;
		}

	}

Action is

    @Action(name = "Action 1")
	public class Action{

		@Do
		public boolean action1() {
			return true;
		}

		@Do(priority = Priority.HIGHEST)
		public void action2() {
		}
	}
    
Rule is

    @Rule(name = "Rule 1", conditions = {Condition.class}, actions = { Action.class })
	public class Rule {
	}

And here we go

    RuleContext context = new RuleContext();
	RulesExecutor executor = new RulesExecutor(context, new Rule());
	executor.executeWhenAllEvaluated();