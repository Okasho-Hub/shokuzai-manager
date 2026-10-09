# --- ステップ 1: Mavenでビルドする ---
FROM maven:3.8.4-openjdk-17 AS builder
WORKDIR /app
COPY . .
# Mavenを使ってWARファイルをビルド
RUN mvn clean package -DskipTests

# --- ステップ 2: Tomcatで動かす ---
FROM tomcat:9.0-jdk17-temurin
# デフォルトのルートアプリを削除
RUN rm -rf /usr/local/tomcat/webapps/ROOT
# ビルドステージで出来上がったWARファイルをコピー
COPY --from=builder /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]