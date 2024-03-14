/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com) All Rights Reserved.
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

import io.ballerina.persist.inflector.Pluralizer;
import io.ballerina.persist.inflector.Singularizer;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * A unit test class for singular to plural functions.
 */
public class SingularPluralTest {

    List<String> singularWords = Arrays.asList(
            "boy", "girl", "bird", "cod", "commerce", "quiz", "lemma", "dingo", "echo", "yes",
            "tornado", "dingo", "bus", "child", "mom", "dad", "bottle", "sticker", "moss",
            "wolf", "wife", "life", "leaf", "woman", "mouse", "goose", "baby", "toy", "kidney",
            "potato", "memo", "stereo", "sheep", "deer", "series", "species", "window", "sticker", "desk",
            "pencil", "cup", "milk", "choice", "box", "thief", "army", "woman", "friend", "daisy",
            "boss", "marsh", "class", "lunch", "belief", "chef", "city", "ray", "photo", "piano",
            "cactus", "focus", "series", "species", "mouse", "foot", "nanny", "study", "fox", "pouch",
            "brush", "quiz", "roof", "truck", "bug", "pen", "book", "vegetable", "chair",
            "medium", "kangaroo", "cherry", "sky", "monkey", "berry", "video", "studio", "mango",
            "tornado", "tuxedo", "volcano", "house", "sister", "item", "thing", "computer", "flower", "roof",
            "bacterium", "have"
    );

    List<String> pluralWords = Arrays.asList(
            "boys", "girls", "birds", "cod", "commerce", "quizzes", "lemmata", "dingoes", "echoes", "yeses",
            "tornadoes", "dingoes", "buses", "children", "moms", "dads", "bottles", "stickers", "mosses",
            "wolves", "wives", "lives", "leaves", "women", "mouses", "geese", "babies", "toys", "kidneys",
            "potatoes", "memos", "stereos", "sheep", "deer", "series", "species", "windows", "stickers", "desks",
            "pencils", "cups", "milks", "choices", "boxes", "thieves", "armies", "women", "friends", "daisies",
            "bosses", "marshes", "classes", "lunches", "beliefs", "chefs", "cities", "rays", "photos", "pianos",
            "cactuses", "focuses", "series", "species", "mouses", "feet", "nannies", "studies", "foxes", "pouches",
            "brushes", "quizzes", "roofs", "trucks", "bugs", "pens", "books", "vegetables", "chairs",
            "mediums", "kangaroos", "cherries", "skies", "monkeys", "berries", "videos", "studios",
            "mangoes", "tornadoes", "tuxedos", "volcanoes", "houses", "sisters", "items", "things", "computers",
            "flowers", "roofs", "bacteria", "have"
    );

    List<String> inputForPluralTest =
            Stream.concat(singularWords.stream(), Stream.of("bacterium", "has")).toList();
    List<String> outputForPluralTest =
            Stream.concat(pluralWords.stream(), Stream.of("bacteria", "have")).toList();
    List<String> inputForSingularTest = Stream.concat(pluralWords.stream(), Stream.of("bacterium")).toList();
    List<String> outputForSingularTest = Stream.concat(singularWords.stream(), Stream.of("bacterium")).toList();

    @Test
    public void testSingularToPlural() {
        long startTime = System.nanoTime();
        List<String> outputs = inputForPluralTest.stream().map(Pluralizer::pluralize).toList();
        assertResults(startTime, outputs, outputForPluralTest);
    }
    @Test(enabled = true)
    public void testPluralToSingular() {
        long startTime = System.nanoTime();
        List<String> outputs = inputForSingularTest.stream().map(Singularizer::singularize).toList();
        assertResults(startTime, outputs, outputForSingularTest);
    }
    private void assertResults(long startTime, List<String> outputs, List<String> expectedOutputs) {
        PrintStream print = System.out;
        long endTime = System.nanoTime();
        // todo: Total time in milliseconds with initial implementation : 62 - 66
        print.println("Total time in milliseconds which takes to convert 100 words to plural: " +
                TimeUnit.MILLISECONDS.convert(endTime - startTime, TimeUnit.NANOSECONDS));
        for (int i = 0; i < outputs.size(); i++) {
            Assert.assertEquals(outputs.get(i), expectedOutputs.get(i));
        }
    }
}
