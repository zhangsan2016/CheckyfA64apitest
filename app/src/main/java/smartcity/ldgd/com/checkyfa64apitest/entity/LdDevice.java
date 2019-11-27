package smartcity.ldgd.com.checkyfa64apitest.entity;

/**
 * Created by ldgd on 2019/11/15.
 * 功能：
 * 说明：设备参数
 */

public class LdDevice {

    // 温度
    private double temperature;
    // 湿度
    private double humidity;
    // 电压
    private double voltage;
    // 电流
    private double electricity;
    // 功率
    private double power;
    // 电能
    private double electricalEnergy;
    // 功率因数
    private double powerFactor;
    // 漏电电流
    private double leakCurrent;
    // 报警状态
    private int alarmStatus;



    public double getVoltage() {
        return voltage;
    }

    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }

    public double getElectricity() {
        return electricity;
    }

    public void setElectricity(double electricity) {
        this.electricity = electricity;
    }

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public double getElectricalEnergy() {
        return electricalEnergy;
    }

    public void setElectricalEnergy(double electricalEnergy) {
        this.electricalEnergy = electricalEnergy;
    }

    public double getPowerFactor() {
        return powerFactor;
    }

    public void setPowerFactor(double powerFactor) {
        this.powerFactor = powerFactor;
    }

    public double getLeakCurrent() {
        return leakCurrent;
    }

    public void setLeakCurrent(double leakCurrent) {
        this.leakCurrent = leakCurrent;
    }

    public int getAlarmStatus() {
        return alarmStatus;
    }

    public void setAlarmStatus(int alarmStatus) {
        this.alarmStatus = alarmStatus;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    @Override
    public String toString() {
        return "LdDevice{" +
                "voltage=" + voltage +
                ", electricity=" + electricity +
                ", power=" + power +
                ", electricalEnergy=" + electricalEnergy +
                ", powerFactor=" + powerFactor +
                ", leakCurrent=" + leakCurrent +
                ", alarmStatus=" + alarmStatus +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                '}';
    }
}
