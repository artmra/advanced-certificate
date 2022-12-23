### COMO OBTER OS CERTIFICADOS DOS SERVIDORES

Salve todos os certificados mostrados pela execução do comando abaixo:

```shell
openssl s_client -showcerts -connect <DOMINIO_BASE_SEM_PROTOCOLO>:443
```

Um exemplo de valor para DOMINIO_BASE_SEM_PROTOCOLO é `pbad.labsec.ufsc.br`.

```shell
openssl s_client -showcerts -connect pbad.labsec.ufsc.br:443
```

Em alguns casos(como o do comando acima) a execução do comando pode não retornar todos os certificados da cadeia.
Nesse caso salve o certificado do site(o certificado de profundidade 0)
e obtenha cada um dos certificados da cadeia pelos links contidos na extensão
`Authority Information Access(oid: 1.3.6.1.5.5.7.1.1)` do
certificado; utilize o link associado ao Access Method `CA Issuers (oid: 1.3.6.1.5.5.7.48.2)`.
Utilizar o [KeyStore Explorer](https://keystore-explorer.org/downloads.html) pode ajudar.

Com todos os certificados necessários obtidos execute o seguinte comando para verificar a cadeia:

```shell
openssl verify -CAfile <CERTIFICADO_AC_RAIZ>.cer -untrusted <CERTIFICADO_AC_INTERMEDIARIA_1>.cer <CERTIFICADO_FINAL>.crt
```

Se os certificados estiverem corretos será obtido um output semelhante a esse:

![00](readme_files/images/00.png)


Agora basta adicionar os certificados a keystore do jdk em uso. O [JDK padrão](https://docs.oracle.com/cd/E37670_01/E36387/html/ol_keytool_sec.html) do linux geralmente se encontra em
`/etc/pki/java/cacerts` ou `/usr/java/jdk-11.0.16.1/lib/security/cacerts`. Com o diretório da keystore identificado execute o
seguinte comando:

```shell
sudo keytool -import -alias "<NOME_INTERNO_DO_CERTIFICADO>" -file <CERTIFICATO_DO_SERVICO>.cer \
-keystore /usr/java/jdk-11.0.16.1/lib/security/cacerts
```

### ARQUIVO application.yml

Nessa seção estão disponíveis possíveis valores e explicações para as variáveis de ambiente referenciadas no arquivo
```application.properties```.

| Variável de ambiente | Possível Valor                                                                                                 |
|----------------------|----------------------------------------------------------------------------------------------------------------|
| VERIFIER_ADDRESS     | https://verificador.iti.gov.br/verifier-2.8.1/report                                                           |
| VERIFIER_ADDRESS     | https://verificador.iti.br/report                                                                              |
| SERVER_PORT          | qualquer valor                                                                                                 |
| HAWA_ADDRESS         | qualquer endereço com uma versão do hawa que suporte comunicação sem cmc                                       |
| CITY                 | qualquer valor                                                                                                 |
| STATE                | qualquer valor                                                                                                 |
| RSA_KEY_SIZE         | qualquer tamanho suportado pela versão 1.68 do bouncycastle                                                    |
| TESSERACT_DATAPATH   | endereço com diretório tessdata do tesseract                                                                   |
| DB_ADDRESS           | hostname do db mysql a ser utilizado                                                                           |
| DB_PORT              | porta do db mysql a ser utilizado                                                                              |
| DB_SCHEMA            | nome do esquema da base de dados a ser utilizado                                                               |
| DB_USERNAME          | usuário do db com permissões necessárias                                                                       |
| DB_PASSWORD          | senha do usuário do db com permissões necessárias                                                              |
| KEY_STORE_PATH       | endereço da JKS que contém as chaves utilizadas para assinar os dossiês dos certificados gerados.              |
| KEY_ENTRY            | Nome da entrada na JKS que contém a  chave que será utilizada para assinar    os certificados                  |
| KEY_ENTRY_PASSWORD   | Senha da key entry                                                                                             |
| KEY_STORE_PASSWORD   | Senha da JKS                                                                                                   |
| TESS_DATAPATH        | Caminho para o diretório com as informações de treinamento utilizadas pelo tesseract para realiza  ção do OCR. |
| TESS_OCR_ENGINE_MODE | Engine utilizada pelo Tesseract  para realizar o OCR                                                           |
| TESS_PAGE_SEG_MODE   | Meio pelo qual o Tesseract busca interpretar as informações conti das em uma imagem                            |


### Corpo de requisição e resposta do endpoint /issue

A estrutura do corpo da requisição deste endpoint pode ser observada na tabela abaixo
![01](readme_files/images/01.png)

As possíveis respostas para este enpoint podem ser observadas na tabela abaixo
![02](readme_files/images/02.png)

### Corpo de requisição e resposta do endpoint /revoke

Os parametro da requisição deste endpoint pode ser observada na tabela abaixo
![03](readme_files/images/03.png)

As possíveis respostas para este enpoint podem ser observadas na tabela abaixo
![04](readme_files/images/04.png)

### Corpo de requisição e resposta dos endpoints get-cert, /get-cnh, /get-verifier-response, /get-extracted-cnh-info
e /get-dossier

Os parametro da requisição deste endpoint são os mesmos que os do endpoint revoke.

As possíveis respostas para este enpoint podem ser observadas na tabela abaixo
![05](readme_files/images/05.png)

### Estrutura do objeto CNHServiceIssueResponse
```json
{
    "certB64": "Base64 do certificado emitido",
    "certSerialNumber": "Número serial do certificado emitido"
}
```

### Estrutura do objeto CNHServiceIRevokeResponse
```json
{
    "revoked": true OU false,
    "serialNumber": "Número serial do certificado revogado",
    "certB64": "Base64 do certificado revogado",
    "revocationDate": "Data de revogação do certificado"
}
```

### Estrutura do Dossie assinado

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<DigestDocumentos>
    <CNH>
        Base64 do hash documento de CNH digital
    </CNH>
    <InformacoesExtraidasCNH>
        Base64 do hash das informações extraídas
    </InformacoesExtraidasCNH>
    <CertificadoEmitido>
        Base64 do certificado emitido
    </CertificadoEmitido>
    <VerificadorDeDocumentos>
        <URL>
            endereço do Verificador ITI utilizado
        </URL>
        <Report>
            Base64 do hash do relatório gerado pelo verificador
        </Report>
    </VerificadorDeDocumentos>
    <Signature xmlns="http://www.w3.org/2000/09/xmldsig#">
        Informações da assinatura realizada
    </Signature>
</DigestDocumentos>
```

### Estrutura do objeto Extracted CNH Info assinado
```json
{
    "name": "Nome do dono do documento",
    "docInfo": "Informacoes do documento de identidade",
    "cpf": "Número do cpf",
    "birthDate": "dd/mm/yyyy",
    "birthData": "dd/mm/yyyy, cidade natal, estado natal",
    "fatherName": "Nome do pai",
    "motherName": "Nome da mãe",
    "cnh": "Número da cnh",
    "validity": "dd/mm/yyyy",
    "firstCNHDate": "dd/mm/yyyy",
    "issuePlace": "Local de emissão do documento",
    "issueDate": "dd/mm/yyyy",
    "nationality": "Nacionalidade"
}
```

### Estrutura do relatório do Verificador ITI

A estrutura da resposta da API do Verificador ITI utilizado no desenvolvimento está disponível no seguinte [link](https://pbad.labsec.ufsc.br/codigos-de-referencia/docs/verifier-api/):

### Exemplo de CNHs suportadas

![06](readme_files/images/modelo-01.png)
![07](readme_files/images/modelo-02.png)
![08](readme_files/images/modelo-03.png)
