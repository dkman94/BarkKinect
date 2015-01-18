package app.android.barkinector;

/**
 * Created by deepakkumar on 1/18/15.
 */
public class ListObj {
    private String barName;
    private String barLoc;

    public ListObj(String bName, String bLoc){
        barName = bName;
        barLoc = bLoc;
    }

    public String getBarName(){
        return barName;
    }

    public String getBarLoc(){
        return barLoc;
    }

    public void setBarName(String bn){
        barName = bn;
    }

    public void setBarLock(String addr){
        barLoc = addr;
    }

}
