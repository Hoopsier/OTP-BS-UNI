# What is this and what are the requirements to run the code

Assignments in OTP-1.

You need an AMD gpu and most likely linux to run the commands.

## Technologies & Tools Used

Docker, Java, JavaFX, Junit, MariaDB

## Design Approach & Implementation Method

GUI is made without FXML on javafx. Backend is with mariadb.

## Testing & Quality Assurance Steps

mvn test, mvn javafx:run

## How to Run

```sh
cd Junit
# The container runs as root, so allow root to connect to the X server.
xhost +si:localuser:root
sudo systemctl start mariadb
sudo mariadb < DB.sql
cd ..
nvim .env
# add DISPLAY type and DB_PASSWORD
docker run -it \
     --network host \
     -e DB_HOST=127.0.0.1 \
     -e DB_PORT=3306 \
     -e DB_NAME=temp_temperature \
     -e DB_USER=hoopsy \
     -e DB_PASSWORD=123123 \
     -e DISPLAY="$DISPLAY" \
     -v /tmp/.X11-unix:/tmp/.X11-unix:rw \
     --device /dev/dri \
     otp-bs:latest
```

Alternatively, `compose.yaml` already passes `DISPLAY` and mounts the X11 socket:

```sh
xhost +si:localuser:root
docker compose up --build
```

If JavaFX still reports `Unable to open DISPLAY`, verify that `DISPLAY` is set, that `/tmp/.X11-unix` is mounted into the container, and that `xhost` allows the container's user (`root`).
