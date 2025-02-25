FROM node:14.18.2 as base

# Building the frontend
WORKDIR /usr/src/packages/frontend

COPY . .
RUN yarn

RUN yarn build:docker

#use non-root nginx
FROM bitnami/nginx:latest

USER root
WORKDIR /usr/share/nginx/html

USER 0

COPY --from=base /usr/src/packages/frontend/dist/app .
COPY nginx.conf /etc/nginx/nginx.conf

COPY docker-start.sh .
COPY envsubst /usr/local/bin
RUN chmod +x /usr/local/bin/envsubst
RUN chmod +x docker-start.sh
RUN chmod -R g+rwX /usr/share/nginx/html
CMD bash docker-start.sh
USER 1001

EXPOSE 8080
