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


import java.nio.charset.{Charset, StandardCharsets}
import java.nio.file.{Files, Paths, StandardOpenOption}

// see io.vavr:vavr Generator

val N = 8
val TUPLE_NODE_NAME = "<element>"
val TARGET_MAIN = s"${project.getBasedir()}/src-gen/main/java"
val TARGET_TEST = s"${project.getBasedir()}/src-gen/test/java"
val TARGET_MAIN_RES = s"${project.getBasedir()}/src-gen/main/resources"
val VALIDATION_VERSION = s"${project.getArtifactId()}"
val CHARSET = java.nio.charset.StandardCharsets.UTF_8

val collectionValueExtractors =
  s"""io.vavr.${getBeanValidationVersionName(VALIDATION_VERSION)}.valueextraction.SeqValueExtractor
    |io.vavr.${getBeanValidationVersionName(VALIDATION_VERSION)}.valueextraction.MapKeyExtractor
    |io.vavr.${getBeanValidationVersionName(VALIDATION_VERSION)}.valueextraction.MapValueExtractor
    |io.vavr.${getBeanValidationVersionName(VALIDATION_VERSION)}.valueextraction.MultimapKeyExtractor
    |io.vavr.${getBeanValidationVersionName(VALIDATION_VERSION)}.valueextraction.MultimapValueExtractor
    |io.vavr.${getBeanValidationVersionName(VALIDATION_VERSION)}.valueextraction.EitherLeftExtractor
    |io.vavr.${getBeanValidationVersionName(VALIDATION_VERSION)}.valueextraction.EitherRightExtractor
    |""".stripMargin

// generate extractors
for (t <- 1 to N) genVavrFile(
  s"io.vavr.${getBeanValidationVersionName(VALIDATION_VERSION)}.valueextraction",
  s"Tuple${t}Extractor")(genExtractor(t)
)

// generate service loader file
genFile(TARGET_MAIN_RES, "META-INF/services", getServiceLoaderName(VALIDATION_VERSION)){
  collectionValueExtractors + (for {
    a <- 1 to N
    p <- 1 to a
  } yield s"""io.vavr.${getBeanValidationVersionName(VALIDATION_VERSION)}.valueextraction.Tuple${a}Extractor$$${getNameForPosition(p)}Extractor""").mkString("\n")
}

def getBeanValidationVersionName(validationVersion: String): String = {
  validationVersion match {
    case "vavr-beanvalidation2" => "beanvalidation2"
    case "vavr-beanvalidation3" => "beanvalidation3"
  }
}

def getServiceLoaderName(validationVersion: String): String = {
  validationVersion match {
    case "vavr-beanvalidation2" => "javax.validation.valueextraction.ValueExtractor"
    case "vavr-beanvalidation3" => "jakarta.validation.valueextraction.ValueExtractor"
  }
}

def genExtractor(arity: Int): (String, String) => String = (packageName: String, className: String) => {
  val validationImports = genValidationImports(VALIDATION_VERSION);
  val extractors = (for (pos <- 1 to arity) yield genExtractorForPosition(arity, pos)).mkString("\n")

  raw"""import io.vavr.Tuple$arity;
    |
    |$validationImports
    |
    |
    |public interface $className {
    |$extractors
    |}""".stripMargin
}

def genValidationImports(validationVersion: String): String = {
  validationVersion match {
    case "vavr-beanvalidation2" =>
      """import javax.validation.valueextraction.ExtractedValue;
        |import javax.validation.valueextraction.ValueExtractor;""".stripMargin
    case "vavr-beanvalidation3" =>
      """import jakarta.validation.valueextraction.ExtractedValue;
        |import jakarta.validation.valueextraction.ValueExtractor;""".stripMargin
  }
}

def genExtractorForPosition(arity: Int, pos: Int) = {

  val tupleClass = s"""Tuple$arity"""
  val tupleClassWithWildcardTypeParameter = tupleClass + "<" + (1 to arity).map(_ => "?").mkString(", ") + ">"

  val typeParamResult = (1 until pos).map(_ => "?") ++ Seq("@ExtractedValue ?") ++ ((pos + 1) to arity).map(_ => "?")
  val typeParameter = s"""$tupleClass<${typeParamResult.mkString(", ")}>"""

  s"""
  |    class ${getNameForPosition(pos)}Extractor implements ValueExtractor<$typeParameter> {
  |        @Override
  |        public void extractValues($tupleClassWithWildcardTypeParameter originalValue, ValueReceiver receiver) {
  |            receiver.indexedValue("$TUPLE_NODE_NAME", $pos, originalValue._$pos);
  |        }
  |    }""".stripMargin
}

/**
  * Adds the Vavr header to generated classes.
  *
  * @param packageName Java package name
  * @param className   Simple java class name
  * @param gen         A generator which produces a String.
  */
def genVavrFile(packageName: String, className: String, baseDir: String = TARGET_MAIN)(gen: (String, String) => String, knownSimpleClassNames: List[String] = List()) =
  genJavaFile(baseDir, packageName, className)(
    raw"""|/*  __    __  __  __    __  ___
          | * \  \  /  /    \  \  /  /  __/
          | *  \  \/  /  /\  \  \/  /  /
          | *   \____/__/  \__\____/__/
          | *
          | * Copyright 2014-2018 Vavr, http://vavr.io
          | *
          | * Licensed under the Apache License, Version 2.0 (the "License");
          | * you may not use this file except in compliance with the License.
          | * You may obtain a copy of the License at
          | *
          | *     http://www.apache.org/licenses/LICENSE-2.0
          | *
          | * Unless required by applicable law or agreed to in writing, software
          | * distributed under the License is distributed on an "AS IS" BASIS,
          | * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
          | * See the License for the specific language governing permissions and
          | * limitations under the License.
          | */""".stripMargin
  )(gen)(CHARSET)


/**
  * Generates a Java file.
  *
  * @param packageName Java package name
  * @param className Simple java class name
  * @param classHeader A class file header
  * @param gen A generator which produces a String.
  */
def genJavaFile(baseDir: String, packageName: String, className: String)(classHeader: String)(gen: (String, String) => String, knownSimpleClassNames: List[String] = List())(implicit charset: Charset = StandardCharsets.UTF_8): Unit = {

  // DEV-NOTE: using File.separator instead of "/" does *not* work on windows!
  val dirName = packageName.replaceAll("[.]", "/")
  val fileName = className + ".java"
  val classBody = gen.apply(packageName, className)

  genFile(baseDir, dirName, fileName)(
    raw"""$classHeader
      |package $packageName;
      |/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*\
      |   G E N E R A T O R   C R A F T E D
      |\*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/
      |
      |$classBody""".stripMargin
  )
}


/**
  * Generates a file by writing string contents to the file system.
  *
  * @param baseDir The base directory, e.g. src-gen
  * @param dirName The directory relative to baseDir, e.g. main/java
  * @param fileName The file name within baseDir/dirName
  * @param createOption One of java.nio.file.{StandardOpenOption.CREATE_NEW, StandardOpenOption.CREATE}, default: CREATE_NEW
  * @param contents The string contents of the file
  * @param charset The charset, by default UTF-8
  */
def genFile(baseDir: String, dirName: String, fileName: String, createOption: StandardOpenOption = StandardOpenOption.CREATE_NEW)(contents: => String)(implicit charset: Charset = StandardCharsets.UTF_8): Unit = {
  Files.write(
    Files.createDirectories(Paths.get(baseDir, dirName)).resolve(fileName),
    contents.getBytes(charset),
    createOption, StandardOpenOption.WRITE)
}

def getNameForPosition(pos: Int) = pos match {
  case 1 => "First"
  case 2 => "Second"
  case 3 => "Third"
  case 4 => "Fourth"
  case 5 => "Fifth"
  case 6 => "Sixth"
  case 7 => "Seventh"
  case 8 => "Eighth"
}