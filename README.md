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

| Variável de ambiente | Possível Valor                                                           |
|----------------------|--------------------------------------------------------------------------|
| VERIFIER_ADDRESS     | https://verificador.iti.gov.br/verifier-2.8.1/report                     |
| VERIFIER_ADDRESS     | https://verificador.iti.br/report                                        |
| SERVER_PORT          | qualquer valor                                                           |
| HAWA_ADDRESS         | qualquer endereço com uma versão do hawa que suporte comunicação sem cmc |
| CITY                 | qualquer valor                                                           |
| STATE                | qualquer valor                                                           |
| RSA_KEY_SIZE         | qualquer tamanho suportado pela versão 1.68 do bouncycastle              |
| TESSERACT_DATAPATH   | endereço com diretório tessdata do tesseract                             |
| DB_ADDRESS           | hostname do db mysql a ser utilizado                                     |
| DB_PORT              | porta do db mysql a ser utilizado                                        |
| DB_SCHEMA            | nome do esquema da base de dados a ser utilizado                         |
| DB_USERNAME          | usuário do db com permissões necessárias                                 |
| DB_PASSWORD          | senha do usuário do db com permissões necessárias                        |
