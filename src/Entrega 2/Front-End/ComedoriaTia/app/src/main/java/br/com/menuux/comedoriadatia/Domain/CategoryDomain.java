package br.com.menuux.comedoriadatia.Domain;

import java.io.Serializable;

public class CategoryDomain implements Serializable {
    private String ImagePath;
    private String Name;
    private int Id;

    public CategoryDomain() {
    }

    @Override
    public String toString() {
        return  Name;
    }

    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String imagePath) {
        ImagePath = imagePath;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }
}
