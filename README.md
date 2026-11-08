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

## Minikube startup latency

MariaDB resolves the client address for every new database connection. The
minikube node (`192.168.49.2`) has no reverse DNS entry, so that lookup waits
for a timeout of several seconds. The app used to open a new connection for
every DAO call, which made startup take minutes.

`DBConnection` now uses a HikariCP pool, and the DAOs finish reading a result
set before issuing the next query, so only the first connection pays the DNS
cost. To remove even that delay, enable `skip-name-resolve` in the MariaDB
server configuration and restart MariaDB. With `skip-name-resolve` enabled,
grants match clients by IP, so make sure the `hoopsy` account also exists for
`127.0.0.1` and `192.168.49.2` (or uses `%`).
