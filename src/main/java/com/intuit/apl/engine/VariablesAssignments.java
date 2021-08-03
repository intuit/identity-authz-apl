package com.intuit.apl.engine;

import java.util.ArrayList;
import java.util.PriorityQueue;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.SpelNode;
import org.springframework.expression.spel.standard.SpelExpression;


/**
 * This class is responsible for obtaining which expressions are variable assignment expressions.
 * 
 */
class VariablesAssignments {
  private PriorityQueue<RuleDefinition> ruleDefinitions;
  private ArrayList<Expression> variableAssignments;

  ArrayList<Expression> getVariableAssignments() {
    return variableAssignments;
  }

  VariablesAssignments(PriorityQueue<RuleDefinition> ruleDefinitions,
      ArrayList<Expression> variableAssignments) {
    this.ruleDefinitions = ruleDefinitions;
    this.variableAssignments = variableAssignments;
  }

  /**
   * Check if this node is a variable assignment.
   *
   * @param t SpelNode Expression
   * @return Boolean checking if there is variable assignment operation
   */
  private boolean checkVariableAssignment(SpelNode t) {
    if (t == null) {
      return false;
    }
    String assignOperator = t.getClass().getSimpleName();
    /* If the operator is '=' then the operation is variable assignment */
    return assignOperator.equals("Assign");
  }

  /**
   * Obtaining the variable assignments in a rule.
   * 
   */
  void obtainVariableAssignments() {

    for (RuleDefinition ruleDefinition : ruleDefinitions) {
      if (ruleDefinition.getConditionExpressions() == null) {
        continue;
      }
      for (Expression conditionExpr : ruleDefinition.getConditionExpressions()) {
        SpelExpression exprV = (SpelExpression) conditionExpr;
        SpelNode node = exprV.getAST();
        boolean varAssignment = checkVariableAssignment(node);
        if (varAssignment) {
          variableAssignments.add(conditionExpr);
        }
      }
    }
  }
}
