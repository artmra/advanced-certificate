FROM openjdk:11

#ARG PROJECT_NAME
#ARG INSTANCE_NAME
#
#ENV PROJECT_NAME ${PROJECT_NAME}
#ENV INSTANCE_NAME ${INSTANCE_NAME}

# this is the default variable value. It can be overwriten if necessary
#ENV DEFAULT_DB_FILE_DIR /etc/hawa/default_dbs/

#RUN apt update \
#    && apt install -y mariadb-client

ADD target/emissor-avancado*.jar emissor-avancado.jar

ADD iti-certificates/pbad-labsec-ufsc-br/crt/_.labsec.ufsc.br_RNP_ICPEdu_OV_SSL_CA_2019_.cer pbad.cer
ADD iti-certificates/verificador-iti-gov-br/crt/_.iti.gov.br_Autoridade_Certificadora_do_SERPRO_SSLv1_.cer iti_gov_br.cer
ADD iti-certificates/verificador-iti-br/crt/verificador.iti.br_R3_.cer iti_br.cer

RUN keytool -import -alias "pbad" -file pbad.cer -keystore /usr/local/openjdk-11/lib/security/cacerts -storepass changeit -noprompt

RUN keytool -import -alias "iti_gov_br" -file iti_gov_br.cer -keystore /usr/local/openjdk-11/lib/security/cacerts -storepass changeit -noprompt

RUN keytool -import -alias "iti_br" -file iti_br.cer -keystore /usr/local/openjdk-11/lib/security/cacerts -storepass changeit -noprompt

EXPOSE 8080

CMD ["java", "-jar", "emissor-avancado.jar"]
