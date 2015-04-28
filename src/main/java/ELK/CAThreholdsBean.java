package ELK;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by vipul on 4/28/2015.
 */


@Document(collection = "ConfigureAlarmThresholds")
public class CAThreholdsBean {


    @Id
    private String userId;

    private String instanceName;

    private double cpuThreshold;

    private double memThreshold;

    private double diskThreshold;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public double getCpuThreshold() {
        return cpuThreshold;
    }

    public void setCpuThreshold(double cpuThreshold) {
        this.cpuThreshold = cpuThreshold;
    }

    public double getMemThreshold() {
        return memThreshold;
    }

    public void setMemThreshold(double memThreshold) {
        this.memThreshold = memThreshold;
    }

    public double getDiskThreshold() {
        return diskThreshold;
    }

    public void setDiskThreshold(double diskThreshold) {
        this.diskThreshold = diskThreshold;
    }

    public double getNetThreshold() {
        return netThreshold;
    }

    public void setNetThreshold(double netThreshold) {
        this.netThreshold = netThreshold;
    }

    private double netThreshold;

}
