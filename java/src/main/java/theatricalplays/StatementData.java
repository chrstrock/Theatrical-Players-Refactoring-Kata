package theatricalplays;

import java.util.List;
import java.util.Map;

public class StatementData {
    public String customer;
    public List<EnrichedPerformance> performances;
    public int totalAmount;
    public int totalVolumeCredits;
    public Map<String, Play> plays;

    public StatementData(Invoice invoice, Map<String, Play> plays) {
        this.plays = plays;
        this.customer = invoice.customer;
        this.performances = invoice.performances.stream().map(this::enrichPerformance).toList();
        this.totalAmount = getTotalAmount();
        this.totalVolumeCredits = totalVolumeCredits();
    }

    private EnrichedPerformance enrichPerformance(Performance performance) {
        var result = new EnrichedPerformance();
        result.playID = performance.playID;
        result.audience = performance.audience;
        result.play = playFor(result);
        result.amount = getThisAmount(result);
        result.volumeCredits = volumeCreditsFor(result);
        return result;
    }

    private int getTotalAmount() {
        return this.performances.stream().mapToInt(p -> p.amount).sum();
    }

    private int totalVolumeCredits() {
        return this.performances.stream().mapToInt(p -> p.volumeCredits).sum();
    }

    private int volumeCreditsFor(EnrichedPerformance aPerformance) {
        // add volume credits
        int result = 0;
        result += Math.max(aPerformance.audience - 30, 0);
        // add extra credit for every ten comedy attendees
        if ("comedy".equals(aPerformance.play.type))
            result = (int) (result + Math.floor((double) aPerformance.audience / 5));
        return result;
    }

    private Play playFor(EnrichedPerformance perf) {
        return this.plays.get(perf.playID);
    }

    private int getThisAmount(EnrichedPerformance aPerformance) {
        int result;
        switch (aPerformance.play.type) {
            case "tragedy":
                result = 40000;
                if (aPerformance.audience > 30) {
                    result += 1000 * (aPerformance.audience - 30);
                }
                break;
            case "comedy":
                result = 30000;
                if (aPerformance.audience > 20) {
                    result += 10000 + 500 * (aPerformance.audience - 20);
                }
                result += 300 * aPerformance.audience;
                break;
            default:
                throw new Error("unknown type: %s".formatted(aPerformance.play.type));
        }
        return result;
    }
}
