package com.example.minitask.Model;

public class ScreenEvent {
    public boolean isOn;
    public long timestamp;
    public float batteryPercent;

    public ScreenEvent(boolean isOn, long timestamp, float batteryPercent) {
        this.isOn = isOn;
        this.timestamp = timestamp;
        this.batteryPercent = batteryPercent;
    }
}
