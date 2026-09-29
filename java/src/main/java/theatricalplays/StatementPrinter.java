package theatricalplays;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

public class StatementPrinter {

    private final Invoice invoice;
    private final Map<String, Play> plays;

    public StatementPrinter(Invoice invoice, Map<String, Play> plays) {
        this.invoice = invoice;
        this.plays = plays;
    }

    public String print() {
        StatementData statementData = new StatementData();
        statementData.customer = invoice.customer;
        return renderPlainText(statementData);
    }

    private String renderPlainText(StatementData data) {
        StringBuilder result = new StringBuilder(String.format("Statement for %s%n", data.customer));
        for(var perf : invoice.performances){
            // print line for this order
            result.append(String.format("  %s: %s (%s seats)%n", playFor(perf).name, usd(getThisAmount(perf)), perf.audience));
        }
        result.append(String.format("Amount owed is %s%n", usd(totalAmount())));
        result.append(String.format("You earned %s credits%n", totalVolumeCredits()));
        return result.toString();
    }

    private int totalAmount() {
        var result = 0;
        for(var perf : invoice.performances){
            result += getThisAmount(perf);
        }
        return result;
    }

    private int totalVolumeCredits() {
        var volumeCredits = 0;
        for (var perf : invoice.performances) {
            volumeCredits += volumeCreditsFor(perf);
        }
        return volumeCredits;
    }

    private static String usd(int number) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(number / 100);
    }

    private int volumeCreditsFor(Performance perf) {
        int result = 0;
        result  = Math.max(perf.audience - 30, 0);
        // add extra credit for every ten comedy attendees
        if ("comedy".equals(playFor(perf).type)) result += (int) Math.floor((double) perf.audience / 5);
        return result;
    }

    private Play playFor(Performance perf) {
        return this.plays.get(perf.playID);
    }

    private int getThisAmount(Performance perf) {
        var result = 0;

        switch (playFor(perf).type) {
            case "tragedy" -> {
                result = 40000;
                if (perf.audience > 30) {
                    result += 1000 * (perf.audience - 30);
                }
            }
            case "comedy" -> {
                result = 30000;
                if (perf.audience > 20) {
                    result += 10000 + 500 * (perf.audience - 20);
                }
                result += 300 * perf.audience;
            }
            default -> throw new Error("unknown type: %s".formatted(playFor(perf).type));
        }
        return result;
    }

}
