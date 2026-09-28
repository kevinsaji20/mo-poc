import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CountOccurrence {
    public static void main(String[] args) {
        List<Integer> list = List.of(1, 2, 2, 3, 3, 3);
        Map<Integer, Long> occ = list.stream()
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        Collectors.counting()
                ));
        System.out.println(occ);
    }
}
