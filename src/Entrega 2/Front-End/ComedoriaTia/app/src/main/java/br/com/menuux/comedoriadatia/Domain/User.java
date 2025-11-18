package br.com.menuux.comedoriadatia.Domain;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;

@IgnoreExtraProperties
public class User {
    private String uid;
    private String nome;
    private String sobrenome;
    private String email;
    private String celular;
    private String cpf;
    private String role;

    public User() {
        // Construtor vazio para o Firebase
    }

    // Getters
    public String getUid() {
        return uid;
    }

    @PropertyName("Nome")
    public String getNome() {
        return nome;
    }

    @PropertyName("Sobrenome")
    public String getSobrenome() {
        return sobrenome;
    }

    public String getEmail() {
        return email;
    }

    public String getCelular() {
        return celular;
    }

    public String getCpf() {
        return cpf;
    }

    public String getRole() {
        return role;
    }

    // Setters
    public void setUid(String uid) {
        this.uid = uid;
    }

    @PropertyName("Nome")
    public void setNome(String nome) {
        this.nome = nome;
    }

    @PropertyName("Sobrenome")
    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
