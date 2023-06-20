package ca.cmpt276.parentapp.model.Child;

import androidx.annotation.NonNull;

public class Child {
    private String name;
    private String img;
    private String imgName;

    public Child(String name, String bitmap, String imgName) {
        this.name = name;
        this.img = bitmap;
        this.imgName = imgName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    @NonNull
    @Override
    public String toString() {
        return "Child's Name: " + name;
    }
}
