import java.util.List;

public class FindCommonTwoLists {
    public static void main(String[] args) {
        List<Integer> list1 = List.of(1, 2, 3, 4, 5);
        List<Integer> list2 = List.of(3, 4, 5, 6, 7);

        List<Integer> res = list1.stream()
                .filter(list2::contains)
                .toList();

        List<Integer> res = list

        System.out.println(res);
    }
}
