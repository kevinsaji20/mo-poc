import java.util.List;
import java.util.stream.Collectors;

public class FindNumbersStartingWithOne {
    public static void main(String[] args) {
        List<Integer> list = List.of(10, 15, 20, 31, 45, 100);

        List<Integer> result = list.stream()
                .filter(n -> n.toString().startsWith("1")).toList();
        System.out.println(result);
    }
}
