# ProjetoCloud-2024.2

![Diagrama UML](https://github.com/user-attachments/assets/0bce15e5-7c0e-413d-bf8b-7141b5c9c3b9)
![Captura de tela 2024-08-26 141319](https://github.com/user-attachments/assets/663ef288-9b24-4993-8c12-f6ab6496c6e2)


-- Criação da Tabela Cliente
CREATE TABLE Cliente (
    ClienteID INT PRIMARY KEY AUTO_INCREMENT,
    Nome VARCHAR(100) NOT NULL,
    CPF VARCHAR(11) UNIQUE NOT NULL,
    Endereco VARCHAR(255) NOT NULL,
    Telefone VARCHAR(20),
    Saldo DECIMAL(10, 2) DEFAULT 0.00,
    LimiteCredito DECIMAL(10, 2) DEFAULT 1000.00,
    MetodoNotificacao VARCHAR(10) DEFAULT 'Email',
    CartaoAtivo BOOLEAN DEFAULT TRUE,
    DataCriacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    DataAtualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Criação da Tabela Transacao
CREATE TABLE Transacao (
    TransacaoID INT PRIMARY KEY AUTO_INCREMENT,
    ClienteID INT NOT NULL,
    NumeroCartao VARCHAR(16) NOT NULL,
    CVV VARCHAR(4) NOT NULL,
    Valor DECIMAL(10, 2) NOT NULL,
    Comerciante VARCHAR(100) NOT NULL,
    StatusTransacao VARCHAR(20) NOT NULL,
    DataTransacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ClienteID) REFERENCES Cliente(ClienteID)
);

-- Criação da Tabela Notificacao
CREATE TABLE Notificacao (
    NotificacaoID INT PRIMARY KEY AUTO_INCREMENT,
    ClienteID INT NOT NULL,
    TransacaoID INT,
    Metodo VARCHAR(10) NOT NULL,
    Detalhes VARCHAR(255) NOT NULL,
    DataNotificacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ClienteID) REFERENCES Cliente(ClienteID),
    FOREIGN KEY (TransacaoID) REFERENCES Transacao(TransacaoID)
);

-- Verifica se o CPF é único antes de inserir um novo cliente
CREATE TRIGGER trg_before_insert_cliente
BEFORE INSERT ON Cliente
FOR EACH ROW
BEGIN
    DECLARE cpf_count INT;
    SELECT COUNT(*) INTO cpf_count FROM Cliente WHERE CPF = NEW.CPF;
    IF cpf_count > 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'CPF já existe no sistema.';
    END IF;
END;

-- Verifica as regras antes de uma transação ser autorizada
CREATE TRIGGER trg_before_insert_transacao
BEFORE INSERT ON Transacao
FOR EACH ROW
BEGIN
    DECLARE cartao_ativo BOOLEAN;
    DECLARE saldo DECIMAL(10, 2);
    DECLARE limite_credito DECIMAL(10, 2);
    DECLARE transacoes_count INT;
    DECLARE transacoes_semelhantes_count INT;

    -- Verifica se o cartão está ativo
    SELECT CartaoAtivo INTO cartao_ativo FROM Cliente WHERE ClienteID = NEW.ClienteID;
    IF NOT cartao_ativo THEN
        SET NEW.StatusTransacao = 'Rejeitada';
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cartão não ativo';
    END IF;

    -- Verifica o saldo disponível e o limite de crédito
    SELECT Saldo, LimiteCredito INTO saldo, limite_credito FROM Cliente WHERE ClienteID = NEW.ClienteID;
    IF NEW.Valor > (saldo + limite_credito) THEN
        SET NEW.StatusTransacao = 'Rejeitada';
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Limite insuficiente';
    END IF;

    -- Verifica o histórico de transações dos últimos 2 minutos
    SELECT COUNT(*) INTO transacoes_count 
    FROM Transacao 
    WHERE ClienteID = NEW.ClienteID 
    AND DataTransacao >= (NOW() - INTERVAL 2 MINUTE);

    IF transacoes_count > 3 THEN
        SET NEW.StatusTransacao = 'Rejeitada';
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Alta frequência de transações em curto intervalo';
    END IF;

    -- Verifica transações semelhantes nos últimos 2 minutos
    SELECT COUNT(*) INTO transacoes_semelhantes_count 
    FROM Transacao 
    WHERE ClienteID = NEW.ClienteID 
    AND DataTransacao >= (NOW() - INTERVAL 2 MINUTE)
    AND Valor = NEW.Valor
    AND Comerciante = NEW.Comerciante;

    IF transacoes_semelhantes_count >= 2 THEN
        SET NEW.StatusTransacao = 'Rejeitada';
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Transação duplicada';
    END IF;
    
    -- Se todas as verificações forem bem-sucedidas, a transação é autorizada
    SET NEW.StatusTransacao = 'Autorizada';
END;

-- Atualização das informações do cliente
CREATE TRIGGER trg_before_update_cliente
BEFORE UPDATE ON Cliente
FOR EACH ROW
BEGIN
    -- Verifica se o novo CPF é único
    IF NEW.CPF != OLD.CPF THEN
        DECLARE cpf_count INT;
        SELECT COUNT(*) INTO cpf_count FROM Cliente WHERE CPF = NEW.CPF;
        IF cpf_count > 0 THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'CPF já existe no sistema.';
        END IF;
    END IF;
END;
