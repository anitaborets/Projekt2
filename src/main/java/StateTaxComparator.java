import java.util.Comparator;

public class StateTaxComparator implements Comparator<StateTax> {

    @Override
   public int compare(StateTax o1, StateTax o2) {

       return o2.getStandard_rate() - o1.getStandard_rate();
    }
}
