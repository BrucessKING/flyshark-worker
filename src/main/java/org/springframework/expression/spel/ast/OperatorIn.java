/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.expression.spel.ast;

import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.ast.Operator;
import org.springframework.expression.spel.ast.SpelNodeImpl;
import org.springframework.expression.spel.support.BooleanTypedValue;

import java.util.Map;

/**
 * Represents the between operator. The left operand to between must be a single value and
 * the right operand must be a list - this operator returns true if the left operand is
 * between (using the registered comparator) the two elements in the list. The definition
 * of between being inclusive follows the SQL BETWEEN definition.
 *
 * @author Andy Clement
 * @since 3.0
 */
public class OperatorIn extends Operator{

	public OperatorIn(int startPos, int endPos, SpelNodeImpl... operands) {
		super("in", startPos, endPos, operands);
	}

	@Override
	public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
		Object left = getLeftOperand().getValueInternal(state).getValue();
		Object right = getRightOperand().getValueInternal(state).getValue();
		if (left instanceof String leftString && right instanceof Map map) {
			return BooleanTypedValue.forValue(map.containsKey(leftString));
		}
		return BooleanTypedValue.forValue(false);
	}
}
