Crazy-Rules!
===================
Because rules should be as easy as 1, 2, 3

Condition is 

    @Condition(name = "Condition 1", conjunction = Conjunction.ANY)
	public class ConditionClass {
	
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
	public class ActionClass{

		@Do
		public boolean action1() {
			return true;
		}

		@Do(priority = Priority.HIGHEST)
		public void action2() {
		}
		
	}
    
Rule is

    @Rule(name = "Rule 1", conditions = { ConditionClass.class }, actions = { ActionClass.class })
	public class RuleClass {
	}

And here we go

    RuleContext context = new RuleContext();
	RulesExecutor executor = new RulesExecutor(context, new RuleClass());
	executor.executeWhenAllEvaluated();