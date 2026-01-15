package club.pineclone.toolkit.domain.dto.macro;

public record PercentageDTO(Double percentage, Integer min, Integer max) {

    public double parseDouble() {
        return min + percentage * (max - min);
    }

    public long parseLong() {
        return (long) Math.floor(parseDouble());
    }

    public static PercentageDTO ofValue(double value, int min, int max) {
        double pct = (value - min) / (max - min);
        return new PercentageDTO(pct, min, max);
    }

    public static PercentageDTO fromRealValue(long value, int min, int max) {
        return ofValue(value, min, max);
    }

}
