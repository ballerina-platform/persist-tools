/*
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com) All Rights Reserved.
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.ballerina.persist;

import io.ballerina.persist.inflector.CaseConverter;
import org.testng.annotations.Test;

public class CaseConversionTest {
    private static final String[] randomWords = {
            "this is a test",
            "snake_case",
            "Title Case",
            "dot.case",
            "path/case",
            "sentences case",
            "random case",
            "UPPERCASE",
            "theQuickBrownFoxJumpsOverTheLazyDog",
            "the_quick_brown_fox_jumps_over_the_lazy_dog",
            "the-quick-brown-fox-jumps-over-the-lazy-dog",
            "TheQuickBrownFoxJumpsOverTheLazyDog",
            "THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG",
            "the_quick_brown_foxes_jumps_over_the_lazy_dog",
            "snakesAndLadders",
            "snakes_and_ladders",
            "snakes-and-ladders",
            "SnakesAndLadders",
            "SNAKES_AND_LADDERS"
    };

    private static final String[] camelCase = {
            "thisIsATest",
            "snakeCase",
            "titleCase",
            "dotCase",
            "pathCase",
            "sentencesCase",
            "randomCase",
            "uppercase",
            "theQuickBrownFoxJumpsOverTheLazyDog",
            "theQuickBrownFoxJumpsOverTheLazyDog",
            "theQuickBrownFoxJumpsOverTheLazyDog",
            "theQuickBrownFoxJumpsOverTheLazyDog",
            "theQuickBrownFoxJumpsOverTheLazyDog",
            "theQuickBrownFoxesJumpsOverTheLazyDog",
            "snakesAndLadders",
            "snakesAndLadders",
            "snakesAndLadders",
            "snakesAndLadders",
            "snakesAndLadders"
    };

    private static final String[] pascalCase = {
            "ThisIsATest",
            "SnakeCase",
            "TitleCase",
            "DotCase",
            "PathCase",
            "SentencesCase",
            "RandomCase",
            "Uppercase",
            "TheQuickBrownFoxJumpsOverTheLazyDog",
            "TheQuickBrownFoxJumpsOverTheLazyDog",
            "TheQuickBrownFoxJumpsOverTheLazyDog",
            "TheQuickBrownFoxJumpsOverTheLazyDog",
            "TheQuickBrownFoxJumpsOverTheLazyDog",
            "TheQuickBrownFoxesJumpsOverTheLazyDog",
            "SnakesAndLadders",
            "SnakesAndLadders",
            "SnakesAndLadders",
            "SnakesAndLadders",
            "SnakesAndLadders"
    };

    private static final String[] singularPascalCase = {
            "ThisIsATest",
            "SnakeCase",
            "TitleCase",
            "DotCase",
            "PathCase",
            "SentenceCase",
            "RandomCase",
            "Uppercase",
            "TheQuickBrownFoxJumpOverTheLazyDog",
            "TheQuickBrownFoxJumpOverTheLazyDog",
            "TheQuickBrownFoxJumpOverTheLazyDog",
            "TheQuickBrownFoxJumpOverTheLazyDog",
            "TheQuickBrownFoxJumpOverTheLazyDog",
            "TheQuickBrownFoxJumpOverTheLazyDog",
            "SnakeAndLadder",
            "SnakeAndLadder",
            "SnakeAndLadder",
            "SnakeAndLadder",
            "SnakeAndLadder"
    };

    @Test
    public void testToPascalCase() {
        for (int i = 0; i < randomWords.length; i++) {
            assert CaseConverter.toPascalCase(randomWords[i]).equals(pascalCase[i]);
        }
    }

    @Test
    public void testToCamelCase() {
        for (int i = 0; i < randomWords.length; i++) {
            assert CaseConverter.toCamelCase(randomWords[i]).equals(camelCase[i]);
        }
    }

    @Test
    public void testToSingularPascalCase() {
        for (int i = 0; i < randomWords.length; i++) {
            assert CaseConverter.toSingularPascalCase(randomWords[i]).equals(singularPascalCase[i]);
        }
    }

}
