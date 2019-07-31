
  
  /*  __    __  __  __    __  ___
   * \  \  /  /    \  \  /  /  __/
   *  \  \/  /  /\  \  \/  /  /
   *   \____/__/  \__\____/__/
   *
   * Copyright 2014-2018 Vavr, http://vavr.io
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
  /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*\
     G E N E R A T O R   C R A F T E D
  \*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/
  
  import io.vavr.Tuple3;
  
  import javax.validation.valueextraction.ExtractedValue;
  import javax.validation.valueextraction.ValueExtractor;
  
  
  public interface Tuple3Extractor {
  
  
        class FirstExtractor implements ValueExtractor<Tuple3<@ExtractedValue ?, ?, ?>> {
  
              @Override
              public void extractValues(Tuple3<?, ?, ?> originalValue, ValueReceiver receiver) {
                  receiver.indexedValue("<element>", 1, originalValue._1);
              }
  
          }


        class SecondExtractor implements ValueExtractor<Tuple3<?, @ExtractedValue ?, ?>> {
  
              @Override
              public void extractValues(Tuple3<?, ?, ?> originalValue, ValueReceiver receiver) {
                  receiver.indexedValue("<element>", 2, originalValue._2);
              }
  
          }


        class ThirdExtractor implements ValueExtractor<Tuple3<?, ?, @ExtractedValue ?>> {
  
              @Override
              public void extractValues(Tuple3<?, ?, ?> originalValue, ValueReceiver receiver) {
                  receiver.indexedValue("<element>", 3, originalValue._3);
              }
  
          }
      }
  