package com.example.minitask.Model;

public class ScreenEvent {
    public boolean isOn;
    public long timestamp;
    public float batteryPercent;
    public boolean isAwake;

    public ScreenEvent(boolean isOn, long timestamp, float batteryPercent, boolean isAwake) {
        this.isOn = isOn;
        this.timestamp = timestamp;
        this.batteryPercent = batteryPercent;
        this.isAwake = isAwake;
    }
}
