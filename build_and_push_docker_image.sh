docker build -t flow-app-proxy ./proxy
docker tag flow-app-proxy registry.digitalocean.com/flow-app/flow-app-proxy
docker push registry.digitalocean.com/flow-app/flow-app-proxy
