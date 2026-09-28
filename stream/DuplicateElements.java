import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DuplicateElements {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3, 2, 4, 3, 5));
        list.stream()
                .filter(i -> Collections.frequency(list, i) > 1)
                .distinct()
                .forEach(System.out::println);
    }
}