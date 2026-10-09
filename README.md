# Assignment Description:

Assignments in OTP-1. You need an AMD gpu and most likely linux to run the commands.

## Technologies & Tools Used:
Docker, Java, JavaFX, Junit, MariaDB
## Design Approach & Implementation Method:
GUI is made without FXML on javafx. Backend is with mariadb.
## Testing & Quality Assurance Steps:
mvn test and jenkins start build
## How to Run:
```sh
xhost +local:docker
sudo systemctl start mariadb
sudo mariadb < DB.sql
docker build -t otp_bs:latest .
docker run -it \
     --network host \
     -e DB_HOST=127.0.0.1 \
     -e DB_PORT=3306 \
     -e DB_NAME=temp_temperature \
     -e DB_USER=hoopsy \
     -e DB_PASSWORD=123123 \
     -e DISPLAY=$DISPLAY\
     -v /tmp/.X11-unix:/tmp/.X11-unix:rw \
     --device /dev/dri \
     otp-bs:latest
```
