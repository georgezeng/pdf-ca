sudo docker rm pdf-ca --force
sudo docker run -d --name pdf-ca -p 80:80 pdf-ca
