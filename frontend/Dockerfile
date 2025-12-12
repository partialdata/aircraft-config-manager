# Build Angular assets
FROM node:20-bullseye AS build
WORKDIR /app
COPY package.json package-lock.json* ./
RUN npm install
COPY angular.json tsconfig*.json karma.conf.js ./
COPY src ./src
RUN npm run build -- --configuration production

# Serve with nginx
FROM nginx:1.25-alpine
COPY --from=build /app/dist/acm-frontend/ /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
