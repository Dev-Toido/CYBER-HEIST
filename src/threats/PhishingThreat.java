package threats;

public class PhishingThreat extends Threat {
    private final String fakeEmail;

    public PhishingThreat(String target, int severity, String fakeEmail) {
        super("Phishing Attack", target, severity);
        if (fakeEmail == null || fakeEmail.isBlank()) throw new IllegalArgumentException("Example message cannot be empty.");
        this.fakeEmail = fakeEmail.trim();
    }

    @Override
    public void showThreat() {
        System.out.println("[PHISHING ALERT]");
        super.showThreat();
        System.out.println("Example message: \"" + fakeEmail + "\"");
        System.out.println("Training note : This is a fictional example; do not send it to real users.");
    }

    public String getFakeEmail() { return fakeEmail; }
}
