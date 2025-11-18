package br.com.menuux.comedoriadatia.Domain;

import com.google.firebase.database.Exclude;
import java.io.Serializable;

public class CategoryDomain implements Serializable {
    private String ImagePath;
    private String Name;
    private int Id;

    // Este campo guardará a chave de texto do Firebase (ex: "-Nxyz...")
    // A anotação @Exclude diz ao Firebase para ignorá-lo ao ler/escrever.
    @Exclude
    private String key;

    public CategoryDomain() {
    }

    @Override
    public String toString() {
        return Name;
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

    // Getter e Setter para o ID numérico que já existe no Firebase
    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    // Getter e Setter para a chave de texto, ignorados pelo Firebase
    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }
}
