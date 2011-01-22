package info.xonix.zlo.web.controlsdata.pojos.stats;

/**
 * User: Vovan
 * Date: 22.01.11
 * Time: 17:41
 */
public class DaysNum {
    private int days;
    private String label;

    public DaysNum(int days, String label) {
        this.days = days;
        this.label = label;
    }

    public int getDays() {
        return days;
    }

    public String getLabel() {
        return label;
    }
}
