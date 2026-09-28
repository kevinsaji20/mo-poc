import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StringOps {
    public static void main(String[] args) {
        String str = "programming";

        Map<Character, Long> freq = str.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        LinkedHashMap::new,
                        Collectors.counting()
                ));

        List<Character> muly = freq.entrySet()
                .stream()
                .filter(f -> f.getValue() > 1)
                        .map(Map.Entry::getKey)
                                .toList();

        List<Character> once = freq.entrySet()
                        .stream()
                                .filter(f -> f.getValue() == 1)
                                        .map(Map.Entry::getKey)
                                                .toList();

        System.out.println(freq);
        System.out.println(muly);
        System.out.println(once);
    }
}
