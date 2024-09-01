FROM node:18

WORKDIR /usr/src/app

RUN npm install -g serverless ts-node

COPY package*.json ./
RUN npm install

COPY . .

EXPOSE 3000

CMD ["npm", "run", "start"]