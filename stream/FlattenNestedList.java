import java.util.List;

public class FlattenNestedList {
    public static void main(String[] args) {
        List<List<Integer>> listList = List.of(List.of(1,2), List.of(2,4), List.of(5,6));

        List<Integer> res = listList.stream().flatMap(List::stream).distinct().toList();

        System.out.println(res);
    }
}
