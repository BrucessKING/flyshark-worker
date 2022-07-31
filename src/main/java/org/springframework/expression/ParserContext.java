/*
 * Copyright 2002-2017 the original author or authors.
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

package org.springframework.expression;

/**
 * Input provided to an org.springframework.org.springframework.expression parser that can influence an org.springframework.org.springframework.expression
 * parsing/compilation routine.
 *
 * @author Keith Donald
 * @author Andy Clement
 * @since 3.0
 */
public interface ParserContext {

	/**
	 * Whether or not the org.springframework.org.springframework.expression being parsed is a template. A template org.springframework.org.springframework.expression
	 * consists of literal text that can be mixed with evaluatable blocks. Some examples:
	 * <pre class="code">
	 * 	   Some literal text
	 *     Hello #{name.firstName}!
	 *     #{3 + 4}
	 * </pre>
	 * @return true if the org.springframework.org.springframework.expression is a template, false otherwise
	 */
	boolean isTemplate();

	/**
	 * For template expressions, returns the prefix that identifies the start of an
	 * org.springframework.org.springframework.expression block within a string. For example: "${"
	 * @return the prefix that identifies the start of an org.springframework.org.springframework.expression
	 */
	String getExpressionPrefix();

	/**
	 * For template expressions, return the prefix that identifies the end of an
	 * org.springframework.org.springframework.expression block within a string. For example: "}"
	 * @return the suffix that identifies the end of an org.springframework.org.springframework.expression
	 */
	String getExpressionSuffix();


	/**
	 * The default ParserContext implementation that enables template org.springframework.org.springframework.expression
	 * parsing mode. The org.springframework.org.springframework.expression prefix is "#{" and the org.springframework.org.springframework.expression suffix is "}".
	 * @see #isTemplate()
	 */
	org.springframework.expression.ParserContext TEMPLATE_EXPRESSION = new org.springframework.expression.ParserContext() {

		@Override
		public boolean isTemplate() {
			return true;
		}

		@Override
		public String getExpressionPrefix() {
			return "#{";
		}

		@Override
		public String getExpressionSuffix() {
			return "}";
		}
	};

}
