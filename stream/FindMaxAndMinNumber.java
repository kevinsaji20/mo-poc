import java.util.List;

public class FindMaxAndMinNumber {
    public static void main(String[] args) {
        List<Integer> list = List.of(10, 5, 30, 20, 15);
        Integer min = list.stream().min(Integer::compareTo).orElse(null);
        Integer max = list.stream().max(Integer::compareTo).orElse(null);
        System.out.println("min: " + min + " " + "max: " + max);
    }
}
