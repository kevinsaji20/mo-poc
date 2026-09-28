import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FirstRepeatedCharacter {
    public static Character findFirstRepeatedCharacter(String str) {
        return str.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        LinkedHashMap::new,
                        Collectors.counting()
                ))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }
    public static void main(String[] args) {
        String str = "swiss";
        Character result = findFirstRepeatedCharacter(str);
        System.out.println(result);
    }
}