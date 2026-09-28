import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.LinkedHashMap;
import java.util.Map;

public class FirstNonRepeatedCharactor {
    private static Character findFirstNonRepeatedChar(String str) {
        return str.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        LinkedHashMap::new,
                        Collectors.counting()
                ))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() == 1)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    public static void main(String[] args) {
        String str = "swiss";
        Character result = findFirstNonRepeatedChar(str);
        System.out.println("The first non-repeated character is: " + result);
    }
}