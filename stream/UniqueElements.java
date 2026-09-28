import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class UniqueElements {
    public static void main(String[] args) {
        List<Integer> list = Arrays.asList(1, 2, 2, 3, 4, 4);
        List<Integer> uniqueElements = list.stream()
                .filter(i -> Collections.frequency(list, i) == 1)
                .collect(Collectors.toList());
        System.out.println(uniqueElements);
    }
}