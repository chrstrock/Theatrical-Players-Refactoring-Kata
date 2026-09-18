package theatricalplays;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

public class StatementPrinter {

    private Invoice invoice;
    private Map<String, Play> plays;

    public String print(Invoice invoice, Map<String, Play> plays) {
        this.invoice = invoice;
        this.plays = plays;
        var totalAmount = 0;
        StringBuilder result = new StringBuilder(String.format("Statement for %s%n", invoice.customer));

        for (var perf : invoice.performances) {
            // print line for this order
            result.append(String.format("  %s: %s (%s seats)%n", playFor(plays, perf).name, usd(amountFor(perf)), perf.audience));
            totalAmount += amountFor(perf);
        }
        result.append(String.format("Amount owed is %s%n", usd(totalAmount)));
        result.append(String.format("You earned %s credits%n", getVolumeCredits()));
        return result.toString();
    }

    private int getVolumeCredits() {
        var volumeCredits = 0;
        for (var perf : invoice.performances) {
            volumeCredits += getVolumeCredits(perf);
        }
        return volumeCredits;
    }

    String usd(long number){
        return NumberFormat.getCurrencyInstance(Locale.US).format(number / 100);
    }
    private int getVolumeCredits(Performance perf) {
        int result = 0;
        result += Math.max(perf.audience - 30, 0);
        // add extra credit for every ten comedy attendees
        if ("comedy".equals(playFor(this.getPlays(), perf).type)) result = (int) (result + (double) (perf.audience / 5));
        return result;
    }

    private static Play playFor(Map<String, Play> plays, Performance perf) {
        return plays.get(perf.playID);
    }

    private int amountFor(Performance perf) {
        var result = 0;

        switch (playFor(this.getPlays(), perf).type) {
            case "tragedy":
                result = 40000;
                if (perf.audience > 30) {
                    result += 1000 * (perf.audience - 30);
                }
                break;
            case "comedy":
                result = 30000;
                if (perf.audience > 20) {
                    result += 10000 + 500 * (perf.audience - 20);
                }
                result += 300 * perf.audience;
                break;
            default:
                throw new Error("unknown type: %s".formatted(playFor(this.getPlays(), perf).type));
        }
        return result;
    }

    public Map<String, Play> getPlays() {
        return plays;
    }

    public void setPlays(Map<String, Play> plays) {
        this.plays = plays;
    }
}
