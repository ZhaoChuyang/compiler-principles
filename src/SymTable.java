import java.util.ArrayList;
import java.util.List;

public class SymTable {

    private static List<Triplet<String, String, Integer>> symbol_list= new ArrayList<>();

    static{
        symbol_list.add(new Triplet<String, String, Integer>(null, "nulsym", 1));
        symbol_list.add(new Triplet<String, String, Integer>(null, "identsym", 2));
        symbol_list.add(new Triplet<String, String, Integer>(null, "numbersym", 3));
        symbol_list.add(new Triplet<String, String, Integer>("+", "plussym", 4));
        symbol_list.add(new Triplet<String, String, Integer>("-", "minussym", 5));
        symbol_list.add(new Triplet<String, String, Integer>("*", "multsym", 6));
        symbol_list.add(new Triplet<String, String, Integer>("/", "slashsym", 7));
        symbol_list.add(new Triplet<String, String, Integer>("odd", "oddsym", 8));
        symbol_list.add(new Triplet<String, String, Integer>("=", "eqlsym", 9));
        symbol_list.add(new Triplet<String, String, Integer>("<>", "neqsym", 10));
        symbol_list.add(new Triplet<String, String, Integer>("<", "lessym", 11));
        symbol_list.add(new Triplet<String, String, Integer>("<=", "leqsym", 12));
        symbol_list.add(new Triplet<String, String, Integer>(">", "gtrsym", 13));
        symbol_list.add(new Triplet<String, String, Integer>(">=", "geqsym", 14));
        symbol_list.add(new Triplet<String, String, Integer>("(", "lparentsym", 15));
        symbol_list.add(new Triplet<String, String, Integer>(")", "rparentsym", 16));
        symbol_list.add(new Triplet<String, String, Integer>(",", "commasym", 17));
        symbol_list.add(new Triplet<String, String, Integer>(";", "semicolomsym", 18));
        symbol_list.add(new Triplet<String, String, Integer>(".", "periodsym", 19));
        symbol_list.add(new Triplet<String, String, Integer>(":=", "becomessym", 20));
        symbol_list.add(new Triplet<String, String, Integer>("begin", "beginsym", 21));
        symbol_list.add(new Triplet<String, String, Integer>("end", "endsym", 22));
        symbol_list.add(new Triplet<String, String, Integer>("if", "ifsym", 23));
        symbol_list.add(new Triplet<String, String, Integer>("then", "thensym", 24));
        symbol_list.add(new Triplet<String, String, Integer>("while", "whilesym", 25));
        symbol_list.add(new Triplet<String, String, Integer>("do", "dosym", 26));
        symbol_list.add(new Triplet<String, String, Integer>("call", "callsym", 27));
        symbol_list.add(new Triplet<String, String, Integer>("const", "constsym", 28));
        symbol_list.add(new Triplet<String, String, Integer>("var", "intsym", 29)); // change int to var
        symbol_list.add(new Triplet<String, String, Integer>("procedure", "procsym", 30));
        symbol_list.add(new Triplet<String, String, Integer>("out", "outsym", 31));
        symbol_list.add(new Triplet<String, String, Integer>("in", "insym", 32));
        symbol_list.add(new Triplet<String, String, Integer>("else", "elsesym", 33));
        symbol_list.add(new Triplet<String, String, Integer>("write", "writesym", 34));
        symbol_list.add(new Triplet<String, String, Integer>("read", "readsym", 35));

    }

    public static int get_internal_val(String internal_name){
        for(Triplet a : symbol_list){
            if(a.getSecond() == internal_name) {
                return (int)a.getThird();
            }
        }
        throw new RuntimeException("Internal name " + internal_name + " not found in valid symbol list, please check it.");
    }

    public static String get_internal_name(String symbol){
        for(Triplet a : symbol_list){
            if(a.getFirst() != null && a.getFirst().equals(symbol)) {
                return (String)a.getSecond();
            }
        }
        throw new RuntimeException("Symbol " + symbol+ " not found in valid symbol list, please check it.");
    }

    public static boolean is_symbol(String word){
        for(Triplet a : symbol_list){
            if(a.getFirst() != null && a.getFirst().equals(word)) return true;
        }
        return false;
    }
}

class Triplet<T, U, V> {

    private final T first;
    private final U second;
    private final V third;

    Triplet(T first, U second, V third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public T getFirst() { return first; }
    public U getSecond() { return second; }
    public V getThird() { return third; }

}