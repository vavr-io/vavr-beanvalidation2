/*  __    __  __  __    __  ___
 * \  \  /  /    \  \  /  /  __/
 *  \  \/  /  /\  \  \/  /  /
 *   \____/__/  \__\____/__/
 *
 * Copyright 2014-2019 Vavr, http://vavr.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.vavr.beanvalidation2.valueextraction;

import io.vavr.control.Either;

import javax.validation.valueextraction.ExtractedValue;
import javax.validation.valueextraction.ValueExtractor;

public class EitherLeftExtractor implements ValueExtractor<Either<@ExtractedValue ?, ?>> {
	private static final String NODE_NAME = "<left element>";

	@Override
	public void extractValues(Either<?, ?> originalValue, ValueReceiver receiver) {
		if (originalValue.isLeft()) {
			receiver.value(NODE_NAME, originalValue.getLeft());
		} else {
			receiver.value(NODE_NAME, null);
		}
	}
}
