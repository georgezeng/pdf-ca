mkdir app
if [ ! -d "pdf-ca" ]; then
  docker run --rm -it -v ~/app/pdf-ca:/git alpine/git clone https://github.com/georgezeng/pdf-ca.git
else
  cd ~/app/pdf-ca && docker run --rm -it -v ~/app/pdf-ca:/git alpine/git pull
fi
docker run --rm -it -v ~/.m2:/root/.m2 -v "$(pwd)":/usr/src/maven -w /usr/src/maven maven mvn clean verify
cd ~/app/pdf-ca
docker build -t pdf-ca --no-cache