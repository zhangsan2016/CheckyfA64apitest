package smartcity.ldgd.com.checkyfa64apitest.entity;

/**
 * Created by ldgd on 2019/11/15.
 * 功能：
 * 说明：设备参数
 */

public class LdDevice {

    // 电压
    private float voltage;
    // 电流
    private float electricity;
    // 功率
    private float power;
    // 电能
    private float electricalEnergy;
    // 功率因数
    private float powerFactor;
    // 漏电流
    private float leakCurrent;
    // 报警状态
    private int alarmStatus;



    public float getVoltage() {
        return voltage;
    }

    public void setVoltage(float voltage) {
        this.voltage = voltage;
    }

    public float getElectricity() {
        return electricity;
    }

    public void setElectricity(float electricity) {
        this.electricity = electricity;
    }

    public float getPower() {
        return power;
    }

    public void setPower(float power) {
        this.power = power;
    }

    public float getElectricalEnergy() {
        return electricalEnergy;
    }

    public void setElectricalEnergy(float electricalEnergy) {
        this.electricalEnergy = electricalEnergy;
    }

    public float getPowerFactor() {
        return powerFactor;
    }

    public void setPowerFactor(float powerFactor) {
        this.powerFactor = powerFactor;
    }

    public float getLeakCurrent() {
        return leakCurrent;
    }

    public void setLeakCurrent(float leakCurrent) {
        this.leakCurrent = leakCurrent;
    }


    public int getAlarmStatus() {
        return alarmStatus;
    }

    public void setAlarmStatus(int alarmStatus) {
        this.alarmStatus = alarmStatus;
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
                '}';
    }
}
