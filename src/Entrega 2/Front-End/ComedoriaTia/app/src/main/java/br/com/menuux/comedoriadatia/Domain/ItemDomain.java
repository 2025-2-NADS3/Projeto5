package br.com.menuux.comedoriadatia.Domain;

import java.io.Serializable;

public class ItemDomain implements Serializable {
    private String Description;
    private boolean BestFood;
    private int Price;
    private String ImagePath;
    private int Star;
    private int Time;
    private String Title;
    private int CategoryId;
    private int weight;


    public ItemDomain() {
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public boolean isBestFood() {
        return BestFood;
    }

    public void setBestFood(boolean bestFood) {
        BestFood = bestFood;
    }

    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        Price = price;
    }

    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String imagePath) {
        ImagePath = imagePath;
    }

    public int getStar() {
        return Star;
    }

    public void setStar(int star) {
        Star = star;
    }

    public int getTime() {
        return Time;
    }

    public void setTime(int time) {
        Time = time;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public int getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(int categoryId) {
        CategoryId = categoryId;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
