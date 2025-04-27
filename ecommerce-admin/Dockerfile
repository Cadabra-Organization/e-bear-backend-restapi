FROM node:latest

WORKDIR /app

COPY package.json package-lock.json ./
RUN npm install

COPY . ./
RUN npm run build

# serve 설치 (dist 폴더 서빙용)
RUN npm install -g serve

EXPOSE 4000

CMD ["serve", "-s", "dist", "-l", "4000"]