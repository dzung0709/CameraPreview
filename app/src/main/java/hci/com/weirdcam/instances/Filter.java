package hci.com.weirdcam.instances;

public class Filter {
    String name;
    int photoId;

    public Filter(String name, int photoId) {
        this.name = name;
        this.photoId = photoId;
    }

    public int getPhotoId() {
        return this.photoId;
    }

    public String getFilterName() {
        return this.name;
    }
}
