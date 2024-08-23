sudo apt update
sudo apt install xrdp -y
sudo usermod -a -G ssl-cert xrdp
sudo systemctl restart xrdp
sudo ufw allow from 192.168.1.0/24 to any port 3389
sudo ufw reload
