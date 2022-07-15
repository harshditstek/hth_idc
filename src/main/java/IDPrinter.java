import com.hth.id_card.HTH_IDC;

public class IDPrinter {
  public static void main(String[] args) {
    if (args.length != 2) {
      System.exit(0);
    }
    HTH_IDC.begin(args);
  }
}
