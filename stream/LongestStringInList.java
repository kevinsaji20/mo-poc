import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LongestStringInList {
    public static void main(String[] args) {
        List<String> list = List.of("Java", "Spring", "Microservices", "API");

        String res = list.stream()
                .max(Comparator.comparing(String::length))
                .orElse(null);

        List<String> sorted = list.stream()
                .sorted(Comparator.comparing(String::length).reversed()).toList();

        List<String> filter = list.stream()
                        .filter(s -> s.length() > 5).toList();

        String join = list.stream()
                        .collect(Collectors.joining(", "));

        String max = List.of(join.split(", ")).stream()
                        .max(Comparator.comparing(String::length)).orElse(null);

        System.out.println(res);
        System.out.println(sorted);
        System.out.println(filter);
        System.out.println(join);
        System.out.println(max);
    }
}
