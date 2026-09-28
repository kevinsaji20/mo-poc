import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PartitionToEvenAndOdd {
    public static void main(String[] args) {
        List<Integer> list = List.of(1,2,3,4,5,6);
        Map<Boolean, List<Integer>> part = list.stream()
                .collect(Collectors.partitioningBy(n -> n % 2 == 0));
    }
}
