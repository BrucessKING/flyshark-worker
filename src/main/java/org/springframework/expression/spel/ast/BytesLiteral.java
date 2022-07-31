/*
 * Copyright 2002-2021 the original author or authors.
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

import org.springframework.asm.MethodVisitor;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.ast.Literal;

import java.util.Arrays;

/**
 * Expression language AST node that represents a bytes literal.
 *
 * @author Qiang Jin
 */
public class BytesLiteral extends Literal{

	private final TypedValue value;

	public BytesLiteral(Byte[] payload, int startPos, int endPos, String value) {
		super(Arrays.toString(payload), startPos, endPos);
		this.value = new TypedValue(payload);
		this.exitTypeDescriptor = "LB";
	}

	@Override
	public TypedValue getLiteralValue() {
		return this.value;
	}
	@Override
	public String toString() {
		return "'" + getLiteralValue().getValue() + "'";
	}

	@Override
	public boolean isCompilable() {
		return true;
	}

	@Override
	public void generateCode(MethodVisitor mv, CodeFlow cf) {
		mv.visitLdcInsn(this.value.getValue());
		cf.pushDescriptor(this.exitTypeDescriptor);
	}
}
