package synertone.com.satnet.model;

/**
 * Created by snt1231 on 2017/3/28.
 */

public class HomeMenuModel {
    private String title;
    private int imageResId;

    public HomeMenuModel() {
    }
    public HomeMenuModel(String title, int imageResId) {
        this.title = title;
        this.imageResId = imageResId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }
}
