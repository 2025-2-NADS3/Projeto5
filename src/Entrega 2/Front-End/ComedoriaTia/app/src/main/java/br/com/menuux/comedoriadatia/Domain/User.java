package br.com.menuux.comedoriadatia.Domain;

public class User {
    private String nome;
    private String sobrenome;
    private String celular;
    private String cpf;
    private String email;

    public User() {
        // Construtor vazio necessário para o Firebase
    }

    public User(String nome, String sobrenome, String celular, String cpf, String email) {
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.celular = celular;
        this.cpf = cpf;
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
