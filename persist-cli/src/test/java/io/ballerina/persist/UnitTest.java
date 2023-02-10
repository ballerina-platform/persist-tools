package io.ballerina.persist;

import io.ballerina.persist.pluralize.Pluralize;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class UnitTest {

    @Test
    public void testSingularToPlural() {
        PrintStream print = System.out;
        List<String> words = Arrays.asList(
                "boy", "girl", "bird", "cod", "commerce", "quiz", "lemma", "dingo", "echo", "yes",
                "tornado", "have", "dingo", "bus", "child", "mom", "dad", "bottle", "sticker", "moss",
                "wolf", "wife", "life", "leaf", "woman", "mouse", "goose", "baby", "toy", "kidney",
                "potato", "memo", "stereo", "sheep", "deer", "series", "species", "window", "sticker", "desk",
                "pencil", "cup", "milk", "choice", "box", "thief", "army", "woman", "friend", "daisy",
                "boss", "marsh", "class", "lunch", "belief", "chef", "city", "ray", "photo", "piano",
                "cactus", "focus", "series", "species", "mouse", "foot", "nanny", "study", "fox", "pouch",
                "brush", "quiz", "roof", "truck", "bug", "pen", "book", "vegetable", "chair", "bacteria",
                "medium", "bacterium", "kangaroo", "cherry", "sky", "monkey", "berry", "video", "studio", "mango",
                "tornado", "tuxedo", "volcano", "house", "sister", "item", "thing", "computer", "flower", "roof"
                );
        List<String> pluralWords = Arrays.asList(
                "boys", "girls", "birds", "cod", "commerce", "quizzes", "lemmata", "dingoes", "echoes", "yeses",
                "tornadoes", "have", "dingoes", "buses", "children", "moms", "dads", "bottles", "stickers", "mosses",
                "wolves", "wives", "lives", "leaves", "women", "mouses", "geese", "babies", "toys", "kidneys",
                "potatoes", "memos", "stereos", "sheep", "deer", "series", "species", "windows", "stickers", "desks",
                "pencils", "cups", "milks", "choices", "boxes", "thieves", "armies", "women", "friends", "daisies",
                "bosses", "marshes", "classes", "lunches", "beliefs", "chefs", "cities", "rays", "photos", "pianos",
                "cactuses", "focuses", "series", "species", "mouses", "feet", "nannies", "studies", "foxes", "pouches",
                "brushes", "quizzes", "roofs", "trucks", "bugs", "pens", "books", "vegetables", "chairs", "bacteria",
                "mediums", "bacteria", "kangaroos", "cherries", "skies", "monkeys", "berries", "videos", "studios",
                "mangoes", "tornadoes", "tuxedos", "volcanoes", "houses", "sisters", "items", "things", "computers",
                "flowers", "roofs"
                );
        List<String> outputs = new ArrayList<>();
        long startTime = System.nanoTime();
        for (String word: words) {
            outputs.add(Pluralize.pluralize(word));
        }
        long endTime = System.nanoTime();
        // todo: Total time in milliseconds with initial implementation : 62 - 66
        print.println("Total time in milliseconds which takes to convert 100 words to plural: " +
                TimeUnit.MILLISECONDS.convert(endTime - startTime, TimeUnit.NANOSECONDS));
        for (int i = 0; i < outputs.size(); i++) {
            Assert.assertEquals(outputs.get(i), pluralWords.get(i));
        }
    }
}
