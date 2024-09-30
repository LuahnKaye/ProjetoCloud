package br.edu.ibmec.cartao_credito.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Transacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column
    private LocalDateTime dataTransacao;

    @Column
    private double valor;

    @Column
    private String comerciante;
    
    // Construtor adicional sem o id (já que é gerado automaticamente)
    public Transacao(LocalDateTime dataTransacao, double valor, String comerciante) {
        this.dataTransacao = dataTransacao;
        this.valor = valor;
        this.comerciante = comerciante;
    }
}