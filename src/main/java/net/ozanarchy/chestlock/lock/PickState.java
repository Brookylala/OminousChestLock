package net.ozanarchy.chestlock.lock;

public record PickState(
    int rustyLimit,
    int rustyAttempts,
    int normalLimit,
    int normalAttempts,
    int silenceLimit,
    int silenceAttempts,
    int silenceOverLimitAttempts,
    long silencePenaltyTimestamp
) {
    public static PickState empty() {
        return new PickState(-1, 0, -1, 0, -1, 0, 0, 0L);
    }

    public PickState withRustyLimit(int limit) {
        return new PickState(limit, rustyAttempts, normalLimit, normalAttempts, silenceLimit, silenceAttempts, silenceOverLimitAttempts, silencePenaltyTimestamp);
    }

    public PickState withRustyAttempts(int attempts) {
        return new PickState(rustyLimit, attempts, normalLimit, normalAttempts, silenceLimit, silenceAttempts, silenceOverLimitAttempts, silencePenaltyTimestamp);
    }

    public PickState withNormalLimit(int limit) {
        return new PickState(rustyLimit, rustyAttempts, limit, normalAttempts, silenceLimit, silenceAttempts, silenceOverLimitAttempts, silencePenaltyTimestamp);
    }

    public PickState withNormalAttempts(int attempts) {
        return new PickState(rustyLimit, rustyAttempts, normalLimit, attempts, silenceLimit, silenceAttempts, silenceOverLimitAttempts, silencePenaltyTimestamp);
    }

    public PickState withSilenceLimit(int limit) {
        return new PickState(rustyLimit, rustyAttempts, normalLimit, normalAttempts, limit, silenceAttempts, silenceOverLimitAttempts, silencePenaltyTimestamp);
    }

    public PickState withSilenceAttempts(int attempts) {
        return new PickState(rustyLimit, rustyAttempts, normalLimit, normalAttempts, silenceLimit, attempts, silenceOverLimitAttempts, silencePenaltyTimestamp);
    }

    public PickState withSilenceOverLimitAttempts(int attempts) {
        return new PickState(rustyLimit, rustyAttempts, normalLimit, normalAttempts, silenceLimit, silenceAttempts, attempts, silencePenaltyTimestamp);
    }

    public PickState withSilencePenaltyTimestamp(long timestamp) {
        return new PickState(rustyLimit, rustyAttempts, normalLimit, normalAttempts, silenceLimit, silenceAttempts, silenceOverLimitAttempts, timestamp);
    }
}
