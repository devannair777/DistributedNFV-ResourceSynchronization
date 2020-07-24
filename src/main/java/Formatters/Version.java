package Formatters;

import java.util.Random;

public class Version
{
    private Random r = new Random();
    int version;

    public Version()
    {
        this.version = r.nextInt(9000);
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean after(Version v)
    {
        if(this.version >= v.version)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Version{" +
                "version=" + version +
                '}';
    }

    public void inc_version()
    {
        this.version += 5;
    }


}
