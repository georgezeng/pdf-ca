mkdir ~/app
if [ ! -d "pdf-ca" ]; then
  sudo docker run --rm -it -v ~/app:/git alpine/git clone https://github.com/georgezeng/pdf-ca.git
else
  cd ~/app/pdf-ca && sudo docker run --rm -it -v ~/app/pdf-ca:/git alpine/git pull
fi
sudo docker run --rm -it -v ~/.m2:/root/.m2 -v ~/app/pdf-ca:/usr/src/maven -w /usr/src/maven maven mvn clean verify
cd ~/app/pdf-ca
sudo docker build -t pdf-ca --no-cache