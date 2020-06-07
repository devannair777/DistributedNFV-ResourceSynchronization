package Formatters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Config {
    private List<String> interfaces;
    private List<String> mGroups;
    public Config()
    {
        interfaces = new ArrayList<>();
        mGroups = new ArrayList<>();
    }
    public Config(Map<String,List<String>> mp)
    {
        this.interfaces = mp.get("interfaces");
        this.mGroups = mp.get("mGroups");
    }

    public List<String> getmGroups() {
        return mGroups;
    }

    @Override
    public String toString() {
        return "Config{" +
                "interfaces=" + interfaces +
                ", mGroups=" + mGroups +
                '}';
    }

    public void setmGroups(List<String> mGroups) {
        this.mGroups = mGroups;
    }

    public List<String> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<String> interfaces) {
        this.interfaces = interfaces;
    }

}
