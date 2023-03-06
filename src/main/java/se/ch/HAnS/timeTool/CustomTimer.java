package se.ch.HAnS.timeTool;

public class CustomTimer {
    final int LOG_INTERVAL = 20000; // How long time in milliseconds needs to be passed since the last logged time
    private long lastLogged = 0; // Last time something was logged

    // Returns true if enough time has passed to log again, else return false
    public boolean canLog(){
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastLogged) > LOG_INTERVAL;
    }

    // Updates the lastLogged variable to the current time
    public void updateLastLogged(){
        lastLogged = System.currentTimeMillis();
    }

}
