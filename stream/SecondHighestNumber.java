import java.util.Comparator;
import java.util.List;

public class SecondHighestNumber {
    public static void main(String[] args) {
        List<Integer> list = List.of(10, 5, 30, 20, 30, 15);

        Integer result = list.stream()
                .sorted(Comparator.reverseOrder())
                .skip(1)
                .findFirst()
                .orElse(null);

        System.out.println(result);
    }

}
