package cn.singull.bean;

/**
 * Created by xinou03 on 2016/1/18 0018.
 */
public class ShareBean {
    private int sharedImageId;
    private int sharedNameId;
    private int sharedPackageId;

    /**
     * 分享对象
     * @param sharedImageId 分享选项的图片ID
     * @param sharedNameId 分享选项的应用名
     * @param sharedPackageId 分享选项的包名
     */
    public ShareBean(int sharedImageId, int sharedNameId, int sharedPackageId) {
        this.sharedImageId = sharedImageId;
        this.sharedNameId = sharedNameId;
        this.sharedPackageId = sharedPackageId;
    }

    public int getSharedImageId() {
        return sharedImageId;
    }

    public void setSharedImageId(int sharedImageId) {
        this.sharedImageId = sharedImageId;
    }

    public int getSharedNameId() {
        return sharedNameId;
    }

    public void setSharedNameId(int sharedNameId) {
        this.sharedNameId = sharedNameId;
    }

    public int getSharedPackageId() {
        return sharedPackageId;
    }

    public void setSharedPackageId(int sharedPackageId) {
        this.sharedPackageId = sharedPackageId;
    }
}
