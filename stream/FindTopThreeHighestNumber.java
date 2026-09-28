import java.util.Comparator;
import java.util.List;

public class FindTopThreeHighestNumber {
    public static void main(String[] args) {
        List<Integer> list = List.of(10, 50, 20, 80, 40, 70);

        List<Integer> res = list.stream()
                .distinct()
                .sorted(Comparator.reverseOrder())
                .limit( 3)
                .toList();

        System.out.println(res);
    }
}
