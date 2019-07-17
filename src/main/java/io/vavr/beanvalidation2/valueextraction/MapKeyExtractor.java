/*  __    __  __  __    __  ___
 * \  \  /  /    \  \  /  /  __/
 *  \  \/  /  /\  \  \/  /  /
 *   \____/__/  \__\____/__/
 *
 * Copyright 2014-2017 Vavr, http://vavr.io
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

import javax.validation.valueextraction.ExtractedValue;
import javax.validation.valueextraction.ValueExtractor;

import io.vavr.collection.Map;

public class MapKeyExtractor implements ValueExtractor<Map<@ExtractedValue ?, ?>> {

    private static final String MAP_KEY_NODE_NAME = "<map key>";

    @Override
    public void extractValues(Map<?, ?> originalValue, ValueReceiver receiver) {
        originalValue.forEach(e -> receiver.keyedValue(MAP_KEY_NODE_NAME, e._1, e._1));
    }
}
