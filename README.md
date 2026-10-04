# gitignoring target. since it's a pain in the ass to deal with in merge conflicts

## How to run app

docker run -it \
  --network host \
  -e DB_HOST=127.0.0.1 \
  -e DB_PORT=3306 \
  -e DB_NAME=temp_temperature \
  -e DB_USER=hoopsy \
  -e DB_PASSWORD=123123 \
  -e DISPLAY=$DISPLAY \
  -v /tmp/.X11-unix:/tmp/.X11-unix \
  renanhoruz/otp_bs:latest

---
90% sure this is next level it works on my machine bullshit
