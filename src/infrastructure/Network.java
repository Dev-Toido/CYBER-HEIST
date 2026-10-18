package infrastructure;

public class Network {
    private boolean trafficBlocked;
    private boolean webServerOnline = true;
    private boolean mailServerOnline = true;

    public boolean blockTraffic() {
        if (trafficBlocked) return false;
        trafficBlocked = true;
        return true;
    }

    public boolean unblockTraffic() {
        if (!trafficBlocked) return false;
        trafficBlocked = false;
        return true;
    }

    public void showStatus() {
        System.out.println("\n--- SIMULATED NETWORK STATUS ---");
        System.out.println("Firewall / suspicious traffic: " + (trafficBlocked ? "BLOCKED" : "MONITORING / ACTIVE"));
        System.out.println("Web server                  : " + (webServerOnline ? "ONLINE" : "OFFLINE"));
        System.out.println("Mail server                 : " + (mailServerOnline ? "ONLINE" : "OFFLINE"));
    }

    public boolean isTrafficBlocked() { return trafficBlocked; }
    public boolean isWebServerOnline() { return webServerOnline; }
    public boolean isMailServerOnline() { return mailServerOnline; }
}
